package jss.problem.dynamic_problem.rachel_dataset;

import jss.IJob;
import jss.problem.dynamic_problem.IDoubleValueGenerator;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class ProcessingTimeGenerator implements IDoubleValueGenerator {

	private double meanProcessingTime;

	/**
	 * TODO javadoc.
	 * @param mean
	 */
	public ProcessingTimeGenerator(double mean) {
		meanProcessingTime = mean;
	}

	@Override
	public double getDoubleValue(IJob job) {
		// TODO Auto-generated method stub
		return 0;
	}

}
