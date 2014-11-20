package jss.problem.dynamic_problem.rachel_dataset;

import java.util.Random;

import jss.IJob;
import jss.problem.dynamic_problem.IDoubleValueGenerator;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class PenaltyGenerator implements IDoubleValueGenerator {

	private double[] penalties = new double[]{1, 2, 4};
	private double[] probs = new double[]{0.2, 0.8, 1.0};

	private long seed;
	private Random rand;

	/**
	 * TODO javadoc.
	 * @param s
	 */
	public PenaltyGenerator(long s) {
		seed = s;
		rand = new Random(seed);
	}

	@Override
	public double getDoubleValue(IJob job) {
		double prob = rand.nextDouble();

		for (int i = 0; i < probs.length; i++) {
			if (prob < probs[i]) {
				return penalties[i];
			}
		}

		return 1;
	}

	@Override
	public void reset() {
		rand = new Random(seed);
	}

}
