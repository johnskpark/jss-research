package app.evolution.multitask.select;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import app.evolution.multitask.JasimaMultitaskIndividual;
import app.evolution.multitask.MultitaskEvolutionState;
import app.evolution.multitask.MultitaskKozaFitness;
import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

public class AverageFillSelection extends MultitaskTournamentSelection {

	private static final long serialVersionUID = -4710594906194873672L;

	public static final String P_NEIGHBOUR_WEIGHT = "neighbour-weight";

	public static final double DEFAULT_WEIGHT = 0.0;

	private double neighbourWeight;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		Parameter def = defaultBase();

		try {
			neighbourWeight = state.parameters.getDouble(base.push(P_NEIGHBOUR_WEIGHT), def.push(P_NEIGHBOUR_WEIGHT), DEFAULT_WEIGHT);
			state.output.message("AverageFillSelection neighbour weight: " + neighbourWeight);
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
//		List<Integer>[][] indsPerTask = multitaskState.getIndsPerTask();
//
//		List<Integer> neighbours = multitaskState.getSimConfig().getNeighbourScenarios(currentTask);
//
//		Set<Integer> indSet = new LinkedHashSet<>();
//		indSet.addAll(indsPerTask[subpopulation][currentTask]);
//		for (int i = 0; i < neighbours.size(); i++) {
//			indSet.addAll(indsPerTask[subpopulation][neighbours.get(i)]);
//		}
//		List<Integer> indList = new ArrayList<>(indSet);

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
			final int task,
			final int[] tournamentInds,
			final int thread) {
		double[] scores = new double[tournamentInds.length];

		MultitaskEvolutionState multitaskState = (MultitaskEvolutionState) state;
		List<Integer>[][] indsPerTask = multitaskState.getIndsPerTask();
		List<Integer>[][] ranksPerTask = multitaskState.getRanksPerTask();
		List<Integer> neighbours = multitaskState.getSimConfig().getNeighbourScenarios(task);

		int numTasks = 1 + neighbours.size();
		double[][] ranks = new double[numTasks][tournamentInds.length];
		int[] worstRanks = new int[numTasks];

		int[] sumRanks = new int[numTasks];
		int[] nonZeroRankCounts = new int[numTasks];

		// Get the ranks of the individuals in the tournament selection.
//		worstRanks[0] = indsPerTask[subpopulation][task].size();
		int worstIndex = indsPerTask[subpopulation][task].size() - 1;
		worstRanks[0] = ranksPerTask[subpopulation][task].get(worstIndex);
		for (int i = 0; i < tournamentInds.length; i++) {
//			ranks[0][i] = indsPerTask[subpopulation][task].indexOf(tournamentInds[i]) + 1;
			int index = indsPerTask[subpopulation][task].indexOf(tournamentInds[i]) + 1;
			ranks[0][i] = (index != -1) ? ranksPerTask[subpopulation][task].get(index) : 0;

			sumRanks[0] += ranks[0][i];
			nonZeroRankCounts[0] += ((ranks[0][i] != 0) ? 1 : 0);
		}

		for (int i = 1; i < numTasks; i++) {
			int neighbourIndex = neighbours.get(i - 1);
//			worstRanks[i] = indsPerTask[subpopulation][neighbourIndex].size();
			worstIndex = indsPerTask[subpopulation][neighbourIndex].size() - 1;
			worstRanks[i] = ranksPerTask[subpopulation][neighbourIndex].get(worstIndex);

			for (int j = 0; j < tournamentInds.length; j++) {
//				ranks[i][j] = indsPerTask[subpopulation][neighbourIndex].indexOf(tournamentInds[j]) + 1;
				int index = indsPerTask[subpopulation][task].indexOf(tournamentInds[i]) + 1;
				ranks[i][j] = (index != -1) ? ranksPerTask[subpopulation][task].get(index) : 0;

				sumRanks[i] += ranks[i][j];
				nonZeroRankCounts[i] += ((ranks[i][j] != 0) ? 1 : 0);
			}
		}

		// Fill in the missing ranks.
		for (int i = 0; i < numTasks; i++) {
			for (int j = 0; j < tournamentInds.length; j++) {
				if (ranks[i][j] == 0) {
					if (nonZeroRankCounts[i] != 0) {
						ranks[i][j] = 1.0 * sumRanks[i] / nonZeroRankCounts[i];
					} else {
						ranks[i][j] = 1.0 * indsPerTask[subpopulation][task].size() / 2;
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

}
