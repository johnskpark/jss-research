package jss.problem.dynamic_problem.rachel_dataset;

import java.util.Random;

import jss.IJob;
import jss.problem.dynamic_problem.IDoubleValueGenerator;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class JobReadyTimeGenerator implements IDoubleValueGenerator {

	private double poissonMean;

	private long seed;
	private Random rand;

	/**
	 * TODO javadoc.
	 * @param mean
	 * @param s
	 */
	public JobReadyTimeGenerator(double mean, long s) {
		poissonMean = mean;

		seed = s;
		rand = new Random(seed);
	}

	@Override
	public double getDoubleValue(IJob job) {
		double value = 0.0;
		double p = Math.exp(-poissonMean);
		double s = p;
		double u = rand.nextDouble();

		do {
			value++;
			p *= poissonMean / value;
			s += p;
		} while (u > s);

		return value;
	}

	@Override
	public void reset() {
		rand = new Random(seed);
	}

}