package app.evolution.multitask.jump;

import app.evolution.multitask.IMultitaskNeighbourJump;
import app.evolution.multitask.JasimaMultitaskIndividual;
import app.evolution.multitask.MultitaskEvolutionState;
import app.evolution.multitask.MultitaskKozaFitness;
import ec.EvolutionState;
import ec.Individual;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;

public class FitnessProbabilityJump implements IMultitaskNeighbourJump {

	private static final long serialVersionUID = 866072103875458209L;

	public static final String P_SEED = "seed";

	private JasimaMultitaskIndividual[][] bestIndsPerTask;
	private JasimaMultitaskIndividual[][] worstIndsPerTask;

	private int numSubpops;
	private int numTasks;
	private MersenneTwisterFast rand;
	private int initSeed;

	private int probCalcCount = 0;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		// Use the default seed.
		initSeed = state.parameters.getInt((new Parameter(P_SEED)).push(""+0), null);

		rand = new MersenneTwisterFast(initSeed);
	}

	@Override
	public void preprocessing(final EvolutionState state, final int threadnum) {
		if (state.generation == 0) {
			MultitaskEvolutionState multitaskState = (MultitaskEvolutionState) state;

			numSubpops = multitaskState.population.subpops.length;
			numTasks = multitaskState.getNumTasks();

			bestIndsPerTask = new JasimaMultitaskIndividual[numSubpops][numTasks];
			worstIndsPerTask = new JasimaMultitaskIndividual[numSubpops][numTasks];
		} else {
			for (int subpop = 0; subpop < state.population.subpops.length; subpop++) {
				Individual[] inds = state.population.subpops[subpop].individuals;
				for (int i = 0; i < inds.length; i++) {
					JasimaMultitaskIndividual ind = (JasimaMultitaskIndividual) inds[i];

					for (int task = 0; task < numTasks; task++) {
						if (ind.getTaskFitness(task) == MultitaskKozaFitness.NOT_SET) {
							continue;
						}

						if (bestIndsPerTask[subpop][task] == null || ind.taskFitnessBetterThan(bestIndsPerTask[subpop][task], task)) {
							bestIndsPerTask[subpop][task] = ind;
						}

						if (worstIndsPerTask[subpop][task] == null || worstIndsPerTask[subpop][task].taskFitnessBetterThan(ind, task)) {
							worstIndsPerTask[subpop][task] = ind;
						}
					}
				}
			}
		}
	}

	@Override
	public boolean jumpToNeighbour(final EvolutionState state,
			final int subpopulation,
			final int currentTask,
			final int neighbourTask,
			final JasimaMultitaskIndividual ind,
			final int threadnum) {
		// Always guarantee a jump for the first neighbour.
		if (ind.getAssignedTask() == currentTask) {
			return true;
		}

		// Calculate the probability from the fitness using min-max normalisation
		// to bring the fitness value in between 0.0 and 1.0 first.
		double taskFitness = ind.getTaskFitness(currentTask);
		double minFitness = getBestIndsPerTask()[subpopulation][currentTask].getTaskFitness(currentTask);
		double maxFitness = getWorstIndsPerTask()[subpopulation][currentTask].getTaskFitness(currentTask);

		double prob = 1.0 - (taskFitness - minFitness) / (maxFitness - minFitness);

		probabilityOutput(state, prob, taskFitness, maxFitness, minFitness);

		return getRand().nextBoolean(prob);
	}

	@Override
	public void addIndividualToTask(final EvolutionState state,
			final int subpopulation,
			final int task,
			final JasimaMultitaskIndividual ind,
			final int threadnum) {
		if (ind.getTaskFitness(task) == MultitaskKozaFitness.NOT_SET) {
			throw new RuntimeException("Trying to add individual that has not been evaluated to task.");
		}

		if (bestIndsPerTask[subpopulation][task] == null || ind.taskFitnessBetterThan(bestIndsPerTask[subpopulation][task], task)) {
			bestIndsPerTask[subpopulation][task] = ind;
		}

		if (worstIndsPerTask[subpopulation][task] == null || worstIndsPerTask[subpopulation][task].taskFitnessBetterThan(ind, task)) {
			worstIndsPerTask[subpopulation][task] = ind;
		}
	}

	protected void probabilityOutput(EvolutionState state, double prob, double taskFitness, double maxFitness, double minFitness) {
		if (probCalcCount % 100 == 0) {
			state.output.message("FitnessProbabilityJump: Outputting probability " + probCalcCount + " calculated: " + prob + ", fitness: " + taskFitness + ", worst: " + maxFitness + ", best: " + minFitness);
		}
		probCalcCount++;

	}

	protected int getProbCalcCount() {
		return probCalcCount;
	}

	@Override
	public void clear() {
		for (int i = 0; i < numSubpops; i++) {
			for (int j = 0; j < numTasks; j++) {
				bestIndsPerTask[i][j] = null;
				worstIndsPerTask[i][j] = null;
			}
		}

		probCalcCount = 0;
	}

	protected JasimaMultitaskIndividual[][] getBestIndsPerTask() {
		return bestIndsPerTask;
	}

	protected JasimaMultitaskIndividual[][] getWorstIndsPerTask() {
		return worstIndsPerTask;
	}

	protected MersenneTwisterFast getRand() {
		return rand;
	}

	protected int getNumSubpops() {
		return numSubpops;
	}

	protected int getNumTasks() {
		return numTasks;
	}

}
