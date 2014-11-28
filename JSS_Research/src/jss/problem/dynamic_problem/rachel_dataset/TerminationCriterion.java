package jss.problem.dynamic_problem.rachel_dataset;

import jss.problem.dynamic_problem.DynamicInstance;
import jss.problem.dynamic_problem.ITerminationCriterion;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class TerminationCriterion implements ITerminationCriterion {

	private static final int NUM_JOBS_COMPLETED = 2500;

	private DynamicInstance problemInstance;

	/**
	 * TODO javadoc.
	 * @param problem
	 */
	public TerminationCriterion(DynamicInstance problem) {
		problemInstance = problem;
	}

	@Override
	public boolean criterionMet() {
		return problemInstance.getNumJobsCompleted() >= NUM_JOBS_COMPLETED;
	}

}
