package jss.evolution.sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jss.IMachine;

/**
 * I done goofed here. Change this to the proper dataset that's located
 * in the problem.
 *
 * @author parkjohn
 *
 */
public class TwoStaticJSSDataset {

	private static final int NUM_PROBLEMS_PER_CONFIG = 8;
	private static final int NUM_JOBS_PER_PROBLEM = 10;

	private static final int MACHINE1_RANGE = 10;
	private static final int MACHINE1_MIN = 100;

	private static final int MACHINE2_RANGE = 20;
	private static final int MACHINE2_MIN = 200;

	private static final double[][] CATEGORY_RATIOS = new double[][]{
			{0.40, 0.40, 0.15, 0.05},
			{0.50, 0.30, 0.10, 0.10},
			{0.30, 0.50, 0.10, 0.10},
			{0.60, 0.10, 0.25, 0.05},
			{0.50, 0.20, 0.25, 0.05},
			{0.10, 0.60, 0.10, 0.10},
			{0.45, 0.05, 0.45, 0.05},
			{0.35, 0.05, 0.55, 0.05},
			{0.25, 0.05, 0.65, 0.05},
			{0.00, 0.00, 1.00, 0.00},
			{0.00, 0.00, 0.10, 0.90},
			{0.10, 0.10, 0.45, 0.35}
	};

	private Random rand;

	private List<BasicInstance> problems = new ArrayList<BasicInstance>();

	public TwoStaticJSSDataset(int seed) {
		rand = new Random(seed);

		for (int i = 0; i < CATEGORY_RATIOS.length; i++) {
			ProblemFactory factory = new ProblemFactory(
					CATEGORY_RATIOS[i][0],
					CATEGORY_RATIOS[i][1],
					CATEGORY_RATIOS[i][2],
					CATEGORY_RATIOS[i][3]);

			for (int j = 0; j < NUM_PROBLEMS_PER_CONFIG; j++) {
				problems.add(factory.createProblem(rand,
						NUM_JOBS_PER_PROBLEM));
			}
		}
	}

	public List<BasicInstance> getProblems() {
		return problems;
	}

	private class ProblemFactory {
		private double[] probs = new double[4];

		public ProblemFactory(double aOnly, double bOnly, double aToB, double bToA) {
			probs[0] = aOnly;
			probs[1] = probs[0]	+ bOnly;
			probs[2] = probs[1] + aToB;
			probs[3] = probs[2] + bToA;
		}

		public BasicInstance createProblem(Random rand, int numJobs) {
			BasicInstance inst = new BasicInstance();
			inst.addMachine(new BasicMachine());
			inst.addMachine(new BasicMachine());

			for (int i = 0; i < numJobs; i++) {
				BasicJob job = new BasicJob();

				double machineProb = rand.nextDouble();

				IMachine machine;
				double processingTime;

				if (machineProb < probs[0]) {
					processingTime = MACHINE1_RANGE * rand.nextDouble() + MACHINE1_MIN;
					machine = inst.getMachines().get(0);

					job.offerMachine(machine);
					job.setProcessingTime(machine, processingTime);
				} else if (machineProb < probs[1]) {
					processingTime = MACHINE2_RANGE * rand.nextDouble() + MACHINE2_MIN;
					machine = inst.getMachines().get(1);

					job.offerMachine(machine);
					job.setProcessingTime(machine, processingTime);
				} else if (machineProb < probs[2]) {
					processingTime = MACHINE1_RANGE * rand.nextDouble() + MACHINE1_MIN;
					machine = inst.getMachines().get(0);
					job.offerMachine(machine);
					job.setProcessingTime(machine, processingTime);

					processingTime = MACHINE2_RANGE * rand.nextDouble() + MACHINE2_MIN;
					machine = inst.getMachines().get(1);
					job.offerMachine(machine);
					job.setProcessingTime(machine, processingTime);
				} else {
					processingTime = MACHINE2_RANGE * rand.nextDouble() + MACHINE2_MIN;
					machine = inst.getMachines().get(1);
					job.offerMachine(machine);
					job.setProcessingTime(machine, processingTime);

					processingTime = MACHINE1_RANGE * rand.nextDouble() + MACHINE1_MIN;
					machine = inst.getMachines().get(0);
					job.offerMachine(machine);
					job.setProcessingTime(machine, processingTime);
				}

				inst.addJob(job);
			}

			return inst;
		}
	}

}
