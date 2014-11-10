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
 * $Id: AbstractMultiExperiment.java 184 2014-10-24 12:18:52Z THildebrandt@gmail.com $
 *******************************************************************************/
package jasima.core.experiment;

import jasima.core.expExecution.ExperimentExecutor;
import jasima.core.expExecution.ExperimentFuture;
import jasima.core.statistics.SummaryStat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Parent class of an experiment which runs a number of child experiments.
 * 
 * @author Torsten Hildebrandt <hil@biba.uni-bremen.de>
 * @version 
 *          "$Id: AbstractMultiExperiment.java 184 2014-10-24 12:18:52Z THildebrandt@gmail.com $"
 */
public abstract class AbstractMultiExperiment extends Experiment {

	private static final long serialVersionUID = 4018379771127550074L;

	public static final String NUM_TASKS_EXECUTED = "numTasks";

	/**
	 * Complex event object triggered upon sub-experiment completion.
	 */
	public static class BaseExperimentCompleted extends ExperimentEvent {

		public BaseExperimentCompleted(Experiment experimentRun,
				Map<String, Object> results) {
			super();
			this.experimentRun = experimentRun;
			this.results = results;
		}

		public final Experiment experimentRun;
		public final Map<String, Object> results;
	}

	// parameters

	private boolean allowParallelExecution = true;
	private boolean commonRandomNumbers = true;
	private int skipSeedCount = 0;
	private boolean abortUponBaseExperimentAbort = false;
	protected HashSet<String> keepResults = new HashSet<String>();
	private boolean produceAveragedResults = true;

	// fields used during run

	protected UniqueNamesCheckingHashMap detailedResultsNumeric;
	protected UniqueNamesCheckingHashMap detailedResultsOther;
	private Random seedStream;
	protected List<Experiment> experiments;
	private int numTasksExecuted;

	@Override
	public void init() {
		super.init();

		experiments = new ArrayList<Experiment>();
		seedStream = null;

		detailedResultsNumeric = new UniqueNamesCheckingHashMap();
		detailedResultsOther = new UniqueNamesCheckingHashMap();
		numTasksExecuted = 0;

		for (int i = 0; i < getSkipSeedCount(); i++) {
			// throw away seed
			getExperimentSeed();
		}
	}

	@Override
	protected void performRun() {
		do {
			createExperiments();
			executeExperiments();
		} while (hasMoreTasks());
		experiments.clear();
	}

	protected boolean hasMoreTasks() {
		return false;
	}

	protected abstract void createExperiments();

	protected void executeExperiments() {
		try {
			if (isAllowParallelExecution()) {
				// start execution and store process results in the same order
				// as they are stored in tasks
				int n = 0;
				Collection<ExperimentFuture> allFutures = ExperimentExecutor
						.getExecutor().runAllExperiments(experiments);
				Iterator<ExperimentFuture> it = allFutures.iterator();
				while (it.hasNext()) {
					ExperimentFuture f = it.next();
					it.remove();

					getAndStoreResults(experiments.get(n), f);
					experiments.set(n, null);
					n++;

					// check if to abort this experiment, if so cancel all
					// future tasks
					if (aborted != 0) {
						for (ExperimentFuture f2 : allFutures) {
							f2.cancel(true);
						}
						break; // while
					}
				}
			} else {
				// sequential execution
				ExperimentExecutor ex = ExperimentExecutor.getExecutor();
				for (int i = 0; i < experiments.size(); i++) {
					Experiment e = experiments.get(i);
					experiments.set(i, null);

					if (aborted == 0) {
						ExperimentFuture future = ex.runExperiment(e);
						getAndStoreResults(e, future);
					} else {
						break; // for i
					}
				}
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private void getAndStoreResults(Experiment e, ExperimentFuture f)
			throws InterruptedException {
		Map<String, Object> res = f.get();

		numTasksExecuted++;
		storeRunResults(e, res);
		if (numListener() > 0) {
			fire(new BaseExperimentCompleted(e, res));
		}
	}

	protected void configureRunExperiment(Experiment e) {
		long s = getExperimentSeed();
		e.setInitialSeed(s);
		e.nestingLevel(nestingLevel() + 1);

		String name = prefix() + padNumTasks(experiments.size() + 1);
		if (e.getName() != null)
			name = name + "." + e.getName();
		e.setName(name);
	}

	protected long getExperimentSeed() {
		if (isCommonRandomNumbers())
			return getInitialSeed();
		else {
			if (seedStream == null)
				seedStream = new Random(getInitialSeed());
			return seedStream.nextLong();
		}
	}

	protected void storeRunResults(Experiment e, Map<String, Object> r) {
		Integer aborted = (Integer) r.get(Experiment.EXP_ABORTED);
		if (aborted != null) {
			if (aborted.intValue() > 0 && isAbortUponBaseExperimentAbort()) {
				abort();
			}
		}

		for (String key : r.keySet()) {
			Object val = r.get(key);

			if (shouldKeepDetails(key))
				detailedResultsOther.put(key + "." + prefix()
						+ padNumTasks(getNumTasksExecuted()), r.get(key));

			if (isProduceAveragedResults()) {
				if ((val != null)
						&& ((val instanceof SummaryStat) || ((val instanceof Number))))
					handleNumericValue(key, val);
				else
					handleOtherValue(key, val);
			}
		}
	}

	private boolean shouldKeepDetails(String key) {
		for (String s : keepResults) {
			if (s.equals(key) || key.startsWith(s + '.'))
				return true;
		}
		return false;
	}

	private String padNumTasks(int v) {
		int l = String.valueOf(getNumExperiments()).length();

		StringBuilder sb = new StringBuilder(l);
		sb.append(v);
		while (sb.length() < l)
			sb.insert(0, '0');
		return sb.toString();
	}

	public abstract int getNumExperiments();

	public int getNumTasks() {
		return experiments.size();
	}

	public int getNumTasksExecuted() {
		return numTasksExecuted;
	}

	protected abstract String prefix();

	public void abort() {
		this.aborted++;
	}

	/**
	 * Handles arbitrary values "val" by storing them in an object array.
	 */
	protected void handleOtherValue(String key, Object val) {
		if (key.endsWith(EXCEPTION) || key.endsWith(EXCEPTION_MESSAGE)) {
			key = "baseExperiment." + key;
		}

		@SuppressWarnings("unchecked")
		ArrayList<Object> l = (ArrayList<Object>) detailedResultsOther.get(key);
		if (l == null) {
			l = new ArrayList<Object>();
			detailedResultsOther.put(key, l);
		}
		l.add(val);
	}

	/**
	 * Handles a numeric value "val" by averaging it over all runs performed. If
	 * "val" is of type SummaryStat, averaging is performed with its
	 * mean()-value.
	 */
	protected void handleNumericValue(String key, Object val) {
		// ensure there is an entry "key" also in "results"
		if (key.endsWith(RUNTIME) || key.endsWith(NUM_TASKS_EXECUTED)
				|| key.endsWith(EXP_ABORTED)) {
			key = "baseExperiment." + key;
		}

		SummaryStat repValues = (SummaryStat) detailedResultsNumeric.get(key);
		if (repValues == null) {
			repValues = new SummaryStat();
			detailedResultsNumeric.put(key, repValues);
		}

		// store run result, may it be a complex statistic or scalar value
		if (val instanceof SummaryStat) {
			SummaryStat vs = (SummaryStat) val;
			if (vs.numObs() > 0)
				repValues.value(vs.mean());
		} else if (val instanceof Number) {
			Number n = (Number) val;
			repValues.value(n.doubleValue());
		} else
			// should never occur
			throw new AssertionError("Illegal experiment result type: "
					+ val.getClass().getName());
	}

	@Override
	protected void produceResults() {
		super.produceResults();

		for (String key : detailedResultsNumeric.keySet()) {
			Object val = detailedResultsNumeric.get(key);

			if (key.endsWith(NUM_TASKS_EXECUTED))
				key = "baseExperiment." + key;

			// careful: SummaryStat is not immutable, so is it better to create
			// a
			// clone?
			assert val instanceof SummaryStat;

			resultMap.put(key, val);
		}

		for (String key : detailedResultsOther.keySet()) {
			Object val = detailedResultsOther.get(key);

			if (key.endsWith(NUM_TASKS_EXECUTED))
				key = "baseExperiment." + key;

			while (resultMap.containsKey(key))
				key += "@Other";

			if (val instanceof ArrayList) {
				ArrayList<?> l = (ArrayList<?>) val;
				val = l.toArray(new Object[l.size()]);
			}
			resultMap.put(key, val);
		}

		resultMap.put(NUM_TASKS_EXECUTED, getNumTasksExecuted());
	}

	@SuppressWarnings("unchecked")
	@Override
	public AbstractMultiExperiment clone() throws CloneNotSupportedException {
		AbstractMultiExperiment mre = (AbstractMultiExperiment) super.clone();

		mre.keepResults = (HashSet<String>) keepResults.clone();

		return mre;
	}

	public void addKeepResultName(String name) {
		keepResults.add(name);
	}

	public void removeKeepResultName(String name) {
		keepResults.remove(name);
	}

	public boolean isKeepTaskResults() {
		return keepResults.size() > 0;
	}

	public void setAllowParallelExecution(boolean allowParallelExecution) {
		this.allowParallelExecution = allowParallelExecution;
	}

	public boolean isAllowParallelExecution() {
		return allowParallelExecution;
	}

	public void setCommonRandomNumbers(boolean commonRandomNumbers) {
		this.commonRandomNumbers = commonRandomNumbers;
	}

	public boolean isCommonRandomNumbers() {
		return commonRandomNumbers;
	}

	@Override
	public boolean isLeafExperiment() {
		return false;
	}

	public void setSkipSeedCount(int skipSeedCount) {
		this.skipSeedCount = skipSeedCount;
	}

	/*
	 * Before starting throw away this many seed values -- useful to resume
	 * interrupted operastions.
	 */
	public int getSkipSeedCount() {
		return skipSeedCount;
	}

	public void setAbortUponBaseExperimentAbort(
			boolean abortUponBaseExperimentAbort) {
		this.abortUponBaseExperimentAbort = abortUponBaseExperimentAbort;
	}

	public boolean isAbortUponBaseExperimentAbort() {
		return abortUponBaseExperimentAbort;
	}

	public boolean isProduceAveragedResults() {
		return produceAveragedResults;
	}

	public void setProduceAveragedResults(boolean produceAveragedResults) {
		this.produceAveragedResults = produceAveragedResults;
	}

}
