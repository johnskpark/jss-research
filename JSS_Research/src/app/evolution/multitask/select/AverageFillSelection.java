package app.evolution.multitask.select;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import app.evolution.multitask.JasimaMultitaskIndividual;
import app.evolution.multitask.MultitaskEvolutionState;
import app.evolution.multitask.MultitaskKozaFitness;
import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;
import jasima.core.util.Pair;

public class AverageFillSelection extends MultitaskTournamentSelection {

	private static final long serialVersionUID = -4710594906194873672L;

	public static final String P_NEIGHBOUR_WEIGHT = "neighbour-weight";
//	public static final String P_USE_WORST_RANK = "worst-rank";

	public static final double DEFAULT_WEIGHT = 0.0;
//	public static final boolean DEFAULT_USE_WORST_RANK = true;

	private double neighbourWeight;
	private boolean useWorstRank;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		Parameter def = defaultBase();

		try {
			neighbourWeight = state.parameters.getDouble(base.push(P_NEIGHBOUR_WEIGHT), def.push(P_NEIGHBOUR_WEIGHT), DEFAULT_WEIGHT);
			state.output.message("AverageFillSelection neighbour weight: " + neighbourWeight);

//			useWorstRank = state.parameters.getBoolean(base.push(P_USE_WORST_RANK), def.push(P_USE_WORST_RANK), DEFAULT_USE_WORST_RANK);
//			state.output.message("AverageFillSelection use worst rank: " + useWorstRank);
		} catch (NumberFormatException ex) {
			state.output.fatal("Neighbour weight for needs to be defined for the class AverageFillSelection.");
		}
	}

	@Override
	public int produce(final int subpopulation,
			final EvolutionState state,
			final int thread) {
		int currentTask = getCurrentTask();

		MultitaskEvolutionState multitaskState = (MultitaskEvolutionState) state;

		List<Integer> indList = getViableInds(multitaskState, subpopulation, currentTask);

		int size = getTournamentSizeToUse(state.random[thread]);
		int[] tournamentInds = new int[size];

		// Pick the number of individuals randomly from the list, and then fill in the missing rank values.
		for (int i = 0; i < size; i++) {
			int index = state.random[thread].nextInt(indList.size());
			tournamentInds[i] = indList.get(index);
		}

		// Calculate the scores, with the highest score being the best.
		double[] scores = calculateScores(state, subpopulation, currentTask, tournamentInds, thread);

		int bestIndex = 0;
		for (int i = 1; i < size; i++) {
			if (pickWorst) {
				if (scores[bestIndex] > scores[i]) {
					bestIndex = i;
				}
			} else {
				if (scores[bestIndex] < scores[i]) {
					bestIndex = i;
				}
			}
		}

		return tournamentInds[bestIndex];
	}

	private List<Integer> getViableInds(final MultitaskEvolutionState state,
			final int subpopulation,
			final int task) {
		List<Integer> indList = new ArrayList<>();
		List<Integer> neighbours = state.getSimConfig().getNeighbourScenarios(task);

		Individual[] inds = state.population.subpops[subpopulation].individuals;
		for (int i = 0; i < inds.length; i++) {
			JasimaMultitaskIndividual ind = (JasimaMultitaskIndividual) inds[i];

			if (ind.getTaskFitness(task) != MultitaskKozaFitness.NOT_SET) {
				indList.add(i);
			} else {
				boolean found = false;
				for (int n = 0; n < neighbours.size() && !found; n++) {
					if (ind.getTaskFitness(n) != MultitaskKozaFitness.NOT_SET) { found = true; }
				}
				if (found) { indList.add(i); }
			}
		}

		return indList;
	}

	protected double[] calculateScores(final EvolutionState state,
			final int subpopulation,
			final int currentTask,
			final int[] tournamentInds,
			final int thread) {
		double[] scores = new double[tournamentInds.length];

		MultitaskEvolutionState multitaskState = (MultitaskEvolutionState) state;
		List<Integer>[][] indsPerTask = multitaskState.getIndsPerTask();
		List<Integer>[][] ranksPerTask = multitaskState.getRanksPerTask();

		int numTasks = multitaskState.getNumTasks();
		double[][] ranks = new double[numTasks][tournamentInds.length];
		double[] worstRanks = new double[numTasks];
		double[] weights = new double[numTasks];

		int[] sumRanks = new int[numTasks];
		int[] nonZeroRankCounts = new int[numTasks];

		Queue<Pair<Integer, Integer>> taskQueue = new LinkedList<>();
		List<Integer> visitedTasks = new ArrayList<>();
		taskQueue.offer(new Pair<Integer, Integer>(currentTask, -1));
		int taskIndex = 0;

		// Calculate the worst ranks, ranks, non-zero counts and weights.
		weights[0] = 1.0;
		while (!taskQueue.isEmpty()) {
			Pair<Integer, Integer> taskPair = taskQueue.poll();
			if (visitedTasks.contains(taskPair.a)) {
				continue;
			}

			worstRanks[taskIndex] = getWorstRank(state, subpopulation, taskPair.a, indsPerTask, ranksPerTask, thread);
			for (int i = 0; i < tournamentInds.length; i++) {
				int indIndex = indsPerTask[subpopulation][taskPair.a].indexOf(tournamentInds[i]);
				ranks[taskIndex][i] = (indIndex != -1) ? ranksPerTask[subpopulation][taskPair.a].get(indIndex) : 0;

				sumRanks[taskIndex] += ranks[taskIndex][i];
				nonZeroRankCounts[taskIndex] += ((ranks[0][i] != 0) ? 1 : 0);
			}

			// Use the index of the past task to calculate weight.
			weights[taskIndex] = (taskPair.b != -1) ? neighbourWeight * weights[taskPair.b] : 1.0;

			List<Integer> neighbours = multitaskState.getSimConfig().getNeighbourScenarios(taskPair.a);
			for (int i = 0; i < neighbours.size(); i++) {
				int neighbourTask = neighbours.get(i);
				taskQueue.offer(new Pair<Integer, Integer>(neighbourTask, taskIndex));
			}

			visitedTasks.add(taskPair.a);
			taskIndex++;
		}

		// Fill in the missing ranks.
		for (int i = 0; i < numTasks; i++) {
			for (int j = 0; j < tournamentInds.length; j++) {
				if (ranks[i][j] == 0) {
					if (nonZeroRankCounts[i] != 0) {
						ranks[i][j] = 1.0 * sumRanks[i] / nonZeroRankCounts[i];
					} else {
						ranks[i][j] = 1.0 * indsPerTask[subpopulation][currentTask].size() / 2;
					}
				}
			}
		}

		// Calculate the scores.
		for (int i = 0; i < tournamentInds.length; i++) {
			scores[i] = 1.0 * (worstRanks[0] - ranks[0][i]) / worstRanks[0];
			for (int j = 1; j < numTasks; j++) {
				scores[i] += 1.0 * neighbourWeight * (worstRanks[j] - ranks[j][i]) / worstRanks[j];
			}
		}

		return scores;
	}

	protected double getDefaultRank(final EvolutionState state,
			final int subpopulation,
			final int task,
			final List<Integer>[][] indsPerTask,
			final List<Integer>[][] ranksPerTask,
			final int thread) {
		if (useWorstRank) {
			return getWorstRank(state, subpopulation, task, indsPerTask, ranksPerTask, thread);
		} else {
			double averageRank = 0.0;
			List<Integer> ranks = ranksPerTask[subpopulation][task];
			for (Integer rank : ranks) {
				averageRank += rank;
			}
			averageRank = averageRank / ranks.size();
			return averageRank;
		}
	}

	protected double getWorstRank(final EvolutionState state,
			final int subpopulation,
			final int task,
			final List<Integer>[][] indsPerTask,
			final List<Integer>[][] ranksPerTask,
			final int thread) {
		int lastIndex = indsPerTask[subpopulation][task].size() - 1;
		return ranksPerTask[subpopulation][task].get(lastIndex);
	}

}
