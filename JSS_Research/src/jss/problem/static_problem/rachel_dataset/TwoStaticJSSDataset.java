package jss.problem.static_problem.rachel_dataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jss.IDataset;
import jss.IProblemInstance;
import jss.ProblemSize;
import jss.problem.static_problem.StaticInstance;
import jss.problem.static_problem.StaticJob;
import jss.problem.static_problem.StaticMachine;

/**
 * Static dataset that is used for Hunt's paper on evolving optimal
 * dispatching rules for 2 machine JSS problem instances.
 *
 * The full details of the implementation are covered in the paper
 * "Evolving Machine Specific Dispatching Rules for a Two-Machine Job Shop
 * using Genetic Programming" by Hunt et al.
 *
 * @author parkjohn
 *
 */
public class TwoStaticJSSDataset implements IDataset {

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

	private long seed;
	private Random rand;

	private List<IProblemInstance> problems = new ArrayList<IProblemInstance>();

	/**
	 * TODO javadoc.
	 */
	public TwoStaticJSSDataset() {
		seed = System.currentTimeMillis();
		rand = new Random(seed);
	}

	/**
	 * TODO javadoc.
	 * @param seed
	 */
	public TwoStaticJSSDataset(int s) {
		seed = s;
		rand = new Random(seed);
	}

	public void setSeed(long s) {
		seed = s;
		rand = new Random(seed);
	}

	public void generateDataset() {
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

	@Override
	public List<IProblemInstance> getProblems() {
		return problems;
	}

	@Override
	public List<IProblemInstance> getTraining(ProblemSize problemSize) {
		return problems;
	}

	@Override
	public List<IProblemInstance> getTesting() {
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

		// TODO this will probably need to be fixed up later.
		public IProblemInstance createProblem(Random rand, int numJobs) {
			StaticInstance problem = new StaticInstance();

			StaticMachine machine1 = new StaticMachine(0, problem);
			StaticMachine machine2 = new StaticMachine(1, problem);

			problem.addMachine(machine1);
			problem.addMachine(machine2);

			for (int i = 0; i < numJobs; i++) {
				StaticJob job = new StaticJob(i);

				double machineProb = rand.nextDouble();
				double processingTime;

				if (machineProb < probs[0]) {
					processingTime = MACHINE1_RANGE * rand.nextDouble() + MACHINE1_MIN;
					job.offerMachine(machine1);
					job.setProcessingTime(machine1, processingTime);
				} else if (machineProb < probs[1]) {
					processingTime = MACHINE2_RANGE * rand.nextDouble() + MACHINE2_MIN;
					job.offerMachine(machine2);
					job.setProcessingTime(machine2, processingTime);
				} else if (machineProb < probs[2]) {
					processingTime = MACHINE1_RANGE * rand.nextDouble() + MACHINE1_MIN;
					job.offerMachine(machine1);
					job.setProcessingTime(machine1, processingTime);

					processingTime = MACHINE2_RANGE * rand.nextDouble() + MACHINE2_MIN;
					job.offerMachine(machine2);
					job.setProcessingTime(machine2, processingTime);
				} else {
					processingTime = MACHINE2_RANGE * rand.nextDouble() + MACHINE2_MIN;
					job.offerMachine(machine2);
					job.setProcessingTime(machine2, processingTime);

					processingTime = MACHINE1_RANGE * rand.nextDouble() + MACHINE1_MIN;
					job.offerMachine(machine1);
					job.setProcessingTime(machine1, processingTime);
				}

				problem.addJob(job);
			}

			return problem;
		}
	}

}
