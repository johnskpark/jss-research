package jss.problem.dynamic_problem.rachel_dataset;

import jss.IJob;
import jss.problem.dynamic_problem.IDoubleValueGenerator;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class DueDateGenerator implements IDoubleValueGenerator {

	private double[] dueDateTightness;

	/**
	 * TODO javadoc.
	 * @param tightness
	 */
	public DueDateGenerator(double[] tightness) {
		dueDateTightness = tightness;
	}

	@Override
	public double getDoubleValue(IJob job) {
		// TODO Auto-generated method stub
		return 0;
	}

}
