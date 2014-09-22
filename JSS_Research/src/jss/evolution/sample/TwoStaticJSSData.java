package jss.evolution.sample;

import java.util.ArrayList;
import java.util.List;

import ec.EvolutionState;
import ec.gp.GPData;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;

public class TwoStaticJSSData extends GPData {

	private static final int NUM_PROBLEMS_PER_CONFIG = 8;
	private static final int NUM_JOBS_PER_PROBLEM = 10;

	private static final int MACHINE1_RANGE = 10;
	private static final int MACHINE1_MIN = 100;

	private static final int MACHINE2_RANGE = 20;
	private static final int MACHINE2_MIN = 200;

	private List<TwoStaticJSSInstance> problems = new ArrayList<TwoStaticJSSInstance>();

	private double[][] ratios = new double[][]{
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

	@Override
	public void copyTo(final GPData gpd) {
		TwoStaticJSSData data = (TwoStaticJSSData)gpd;
		data.problems = problems;
	}

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		for (int i = 0; i < ratios.length; i++) {
			ProblemFactory factory = new ProblemFactory(
					ratios[i][0],
					ratios[i][1],
					ratios[i][2],
					ratios[i][3]);

			for (int j = 0; j < NUM_PROBLEMS_PER_CONFIG; j++) {
				problems.add(factory.createProblem(state.random[0],
						NUM_JOBS_PER_PROBLEM));
			}
		}
	}

	@Override
	public Object clone() {
		TwoStaticJSSData clone = (TwoStaticJSSData)super.clone();
		clone.problems = new ArrayList<TwoStaticJSSInstance>(problems);
		return clone;
	}

	private class ProblemFactory {
		private double[] probs = new double[4];

		public ProblemFactory(double aOnly, double bOnly, double aToB, double bToA) {
			probs[0] = aOnly;
			probs[1] = probs[0]	+ bOnly;
			probs[2] = probs[1] + aToB;
			probs[3] = probs[2] + bToA;
		}

		public TwoStaticJSSInstance createProblem(MersenneTwisterFast rand, int numJobs) {
			TwoStaticJSSInstance inst = new TwoStaticJSSInstance();

			for (int i = 0; i < numJobs; i++) {
				BasicJob job = new BasicJob();

				double machineProb = rand.nextDouble();
				double processingTime;

				// TODO this is disgusting, even for a hack.
				if (machineProb < probs[0]) {
					processingTime = MACHINE1_RANGE * rand.nextDouble() + MACHINE1_MIN;
					job.offerMachine(inst.getMachine1(), processingTime);
				} else if (machineProb < probs[1]) {
					processingTime = MACHINE2_RANGE * rand.nextDouble() + MACHINE2_MIN;
					job.offerMachine(inst.getMachine2(), processingTime);
				} else if (machineProb < probs[1]) {
					processingTime = MACHINE1_RANGE * rand.nextDouble() + MACHINE1_MIN;
					job.offerMachine(inst.getMachine1(), processingTime);

					processingTime = MACHINE2_RANGE * rand.nextDouble() + MACHINE2_MIN;
					job.offerMachine(inst.getMachine2(), processingTime);
				} else {
					processingTime = MACHINE2_RANGE * rand.nextDouble() + MACHINE2_MIN;
					job.offerMachine(inst.getMachine2(), processingTime);

					processingTime = MACHINE1_RANGE * rand.nextDouble() + MACHINE1_MIN;
					job.offerMachine(inst.getMachine1(), processingTime);
				}

				inst.addJob(job);
			}

			return inst;
		}
	}
}
