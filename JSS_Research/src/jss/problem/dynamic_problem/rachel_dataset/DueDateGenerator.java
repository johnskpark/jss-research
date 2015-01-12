package jss.problem.dynamic_problem.rachel_dataset;

import java.util.Random;

import jss.IJob;
import jss.problem.dynamic_problem.IDoubleValueGenerator;

/**
 * Due date generator that calculates the due date value of a job in a JSS
 * problem instance using the approach specified by Hunt et al.
 *
 * In the paper, three values are defined for the due date. The three values
 * specify the magnitude of the due date compared to the total processing time
 * of the job.
 *
 * @author parkjohn
 *
 */
public class DueDateGenerator implements IDoubleValueGenerator {

	private double[] dueDateTightness;
	private int length;

	private long seed;
	private Random rand;

	/**
	 * Initialise a new instance of the due date generator for dynamic JSS
	 * problem instances.
	 * @param tightness Due date tightness factor.
	 * @param s
	 */
	public DueDateGenerator(double[] tightness, long s) {
		dueDateTightness = tightness;
		length = dueDateTightness.length;

		seed = s;
		rand = new Random(seed);
	}

	@Override
	public double getDoubleValue(IJob job) {
		int index = rand.nextInt(length);
		return job.getReadyTime() + dueDateTightness[index] * job.getRemainingTime();
	}

	@Override
	public void reset() {
		rand = new Random(seed);
	}

}
