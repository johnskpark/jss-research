package app.evolution.multitask.jump;

import app.evolution.multitask.JasimaMultitaskIndividual;
import ec.EvolutionState;
import ec.util.Parameter;

public class FitnessProbabilityJump2 extends FitnessProbabilityJump {

	private static final long serialVersionUID = 5953377944185011148L;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
	}

	@Override
	public boolean jumpToNeighbour(final EvolutionState state,
			final int subpopulation,
			final int currentTask,
			final int neighbourTask,
			final JasimaMultitaskIndividual ind,
			final int threadnum) {
		// Calculate the probability from the fitness using min-max normalisation
		// to bring the fitness value in between 0.0 and 1.0 first.
		double taskFitness = ind.getTaskFitness(currentTask);
		double minFitness = getBestIndsPerTask()[subpopulation][currentTask].getTaskFitness(currentTask);
		double maxFitness = getWorstIndsPerTask()[subpopulation][currentTask].getTaskFitness(currentTask);

		double prob = 1.0 - (taskFitness - minFitness) / (maxFitness - minFitness);

		probabilityOutput(state, prob, taskFitness, maxFitness, minFitness);

		return getRand().nextBoolean(prob);
	}

}
