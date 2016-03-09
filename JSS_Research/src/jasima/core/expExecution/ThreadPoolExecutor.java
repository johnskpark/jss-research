/*******************************************************************************
 * Copyright (c) 2010-2015 Torsten Hildebrandt and jasima contributors
 *
 * This file is part of jasima, v1.2.
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
 *******************************************************************************/
package jasima.core.expExecution;

import jasima.core.experiment.Experiment;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * Default implementation of an ExecutorFactory returning an Executor that uses
 * up to {@code Runtime.getRuntime().availableProcessors()} threads to execute
 * tasks concurrently. This number of threads can be overridden by setting the
 * system property "jasima.core.expExecution.ThreadPoolExecutor.numThreads".
 * </p>
 * <p>
 * In order to prevent starvation of worker threads waiting for sub-experiments
 * to complete, there is a thread pool for each nesting level of experiments.
 * </p>
 * 
 * @author Torsten Hildebrandt
 * @version 
 *          "$Id$"
 */
public class ThreadPoolExecutor extends ExperimentExecutor {

	public static final String POOL_SIZE_SETTING = ThreadPoolExecutor.class
			.getName() + ".numThreads";

	// an executor service for each nesting level
	private Map<Integer, ExecutorService> insts = new HashMap<Integer, ExecutorService>();

	protected ThreadPoolExecutor() {
		super();
	}

	@Override
	public ExperimentFuture runExperiment(final Experiment e,
			final Experiment parent) {
		ExecutorService es = getExecutorInstance(e.nestingLevel());
		return new FutureWrapper(e,
				es.submit(new Callable<Map<String, Object>>() {
					@Override
					public Map<String, Object> call() throws Exception {
						e.runExperiment();
						return e.getResults();
					}
				}));
	}

	@Override
	public synchronized void shutdownNow() {
		for (ExecutorService inst : insts.values()) {
			inst.shutdownNow();
		}
		insts.clear();
	}

	private synchronized ExecutorService getExecutorInstance(int nestingLevel) {
		ExecutorService inst = insts.get(nestingLevel);
		if (inst == null) {
			inst = createExecService(nestingLevel);
			insts.put(nestingLevel, inst);
		}

		return inst;
	}

	private ExecutorService createExecService(final int nestingLevel) {
		int numThreads = Runtime.getRuntime().availableProcessors();
		String sizeStr = System.getProperty(POOL_SIZE_SETTING);
		if (sizeStr != null)
			numThreads = Integer.parseInt(sizeStr.trim());

		ThreadFactory threadFactory = new ThreadFactory() {
			final ThreadFactory defFactory = Executors.defaultThreadFactory();
			final AtomicInteger numCreated = new AtomicInteger(0);

			@Override
			public Thread newThread(Runnable r) {
				Thread t = defFactory.newThread(r);
				t.setDaemon(true);
				t.setName("jasimaWorker-" + nestingLevel + "-"
						+ numCreated.addAndGet(1));
				return t;
			}
		};

		return Executors.newFixedThreadPool(numThreads, threadFactory);
	}

}
