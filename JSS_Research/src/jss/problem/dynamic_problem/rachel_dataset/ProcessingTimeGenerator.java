package jss.problem.dynamic_problem.rachel_dataset;

import java.util.Random;

import jss.IJob;
import jss.problem.dynamic_problem.IDoubleValueGenerator;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class ProcessingTimeGenerator implements IDoubleValueGenerator {

	private double meanProcessingTime;

	private long seed;
	private Random rand;

	/**
	 * TODO javadoc.
	 * @param mean
	 * @param s
	 */
	public ProcessingTimeGenerator(double mean, long s) {
		meanProcessingTime = mean;

		seed = s;
		rand = new Random(seed);
	}

	@Override
	public double getDoubleValue(IJob job) {
		double range = 2 * meanProcessingTime;
		return (int)(range * rand.nextDouble());
	}

	@Override
	public void reset() {
		rand = new Random(seed);
	}

}
