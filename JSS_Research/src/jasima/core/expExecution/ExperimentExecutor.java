/*******************************************************************************
 * Copyright (c) 2010-2013 Torsten Hildebrandt and jasima contributors
 *
 * This file is part of jasima, v1.0.
 *
 * jasima is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * jasima is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with jasima.  If not, see <http://www.gnu.org/licenses/>.
 *
 * $Id: ExperimentExecutor.java 74 2013-01-08 17:31:49Z THildebrandt@gmail.com $
 *******************************************************************************/
package jasima.core.expExecution;

import jasima.core.experiment.Experiment;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Base class for classes executing experiments. This class implements the
 * Abstract Factory pattern, therefore ExperimentExecutor.getExecutor() has to
 * be called to create executor instances. This call is delegated to a
 * non-abstract implementation of ExperimentExecutor. Which ExperimentExecutor
 * to use is determined by a system property "jasima.core.ExperimentExecutor".
 * As a default, a {@link ThreadPoolExecutor} with a number of threads equal to
 * the number of available processors is used.
 * 
 * @author Torsten Hildebrandt
 * @version 
 *          "$Id: ExperimentExecutor.java 74 2013-01-08 17:31:49Z THildebrandt@gmail.com $"
 * @see ThreadPoolExecutor
 */
public abstract class ExperimentExecutor {

	public static final String EXECUTOR_FACTORY = "jasima.core.ExperimentExecutor";
	public static final String DEFAULT_FACTORY = ThreadPoolExecutor.class
			.getName();

	private static ExperimentExecutor execFactoryInst = null;

	public static ExperimentExecutor getExecutor() {
		if (execFactoryInst == null) {
			synchronized (ExperimentExecutor.class) {
				if (execFactoryInst == null) { // double check for thread safety
					String factName = System.getProperty(EXECUTOR_FACTORY,
							DEFAULT_FACTORY);
					try {
						Class<?> c = Class.forName(factName);
						execFactoryInst = (ExperimentExecutor) c.newInstance();
					} catch (Exception ex) {
						throw new RuntimeException(ex);
					}

					// cleanup
					Runtime.getRuntime().addShutdownHook(new Thread() {
						@Override
						public void run() {
							ExperimentExecutor.getExecutor().shutdownNow();
						}
					});
				}
			}
		}

		return execFactoryInst;
	}

	/**
	 * Protected constructor, use {@link #getExecutor()} instead.
	 */
	protected ExperimentExecutor() {
		super();
	}

	public abstract ExperimentFuture runExperiment(Experiment e);

	public abstract void shutdownNow();

	public Collection<ExperimentFuture> runAllExperiments(
			Collection<? extends Experiment> es) {
		ArrayList<ExperimentFuture> res = new ArrayList<ExperimentFuture>(
				es.size());

		for (Experiment e : es) {
			res.add(runExperiment(e));
		}

		return res;
	}

	// use only for testing purposes!
	public static synchronized void clearInst() {
		if (execFactoryInst != null)
			execFactoryInst.shutdownNow();
		execFactoryInst = null;
	}

}
