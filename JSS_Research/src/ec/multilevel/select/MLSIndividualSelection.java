package ec.multilevel.select;

import ec.EvolutionState;
import ec.Individual;
import ec.SelectionMethod;
import ec.multilevel.MLSCoopPopulation;
import ec.multilevel.MLSEvolutionState;
import ec.multilevel.MLSSubpopulation;
import ec.select.SelectDefaults;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;
import ec.util.RandomChoice;

public class MLSIndividualSelection extends SelectionMethod {

	private static final long serialVersionUID = 8108053943862048578L;

	private static final int NOT_SELECTED = -1;

	public static final String P_MULTILEVEL = "multilevel";
    public static final String P_SIZE = "size";

	public static final int INDS_PRODUCED = 1;

	private int baseTournamentSize;
	private double probabilityOfPickingSizePlusOne;

	@Override
	public Parameter defaultBase() {
		return SelectDefaults.base().push(P_MULTILEVEL);
	}

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		Parameter def = defaultBase();

		double val = state.parameters.getDouble(base.push(P_SIZE), def.push(P_SIZE), 1.0);
		if (val < 1.0) {
			state.output.fatal("Tournament size must be >= 1.", base.push(P_SIZE), def.push(P_SIZE));
		} else if (val == (int) val) {
			baseTournamentSize = (int) val;
			probabilityOfPickingSizePlusOne = 0.0;
		} else {
			baseTournamentSize = (int) Math.floor(val);
			probabilityOfPickingSizePlusOne = val - baseTournamentSize;
		}
	}

	/**
	 * TODO javadoc.
	 */
	public int getTournamentSizeToUse(MersenneTwisterFast random) {
		if (probabilityOfPickingSizePlusOne == 0.0) {
			return baseTournamentSize;
		}
		return baseTournamentSize + (random.nextBoolean(probabilityOfPickingSizePlusOne) ? 1 : 0);
	}

	@Override
	public int produce(final int subpopulation,
			final EvolutionState state,
			final int thread) {
		return getIndividualFromCoopPopulation(state, thread);
	}

	@Override
	public int produce(final int min,
			final int max,
			final int start,
			final int subpopulation,
			final Individual[] inds,
			final EvolutionState state,
			final int thread) {
		int n = INDS_PRODUCED;
		if (n < min) n = min;
		if (n > max) n = max;

		MLSCoopPopulation coopPop = ((MLSEvolutionState) state).getCoopPopulation();

		for (int i = 0; i < n; i++) {
			int indIndex = getGroupedIndividual(state, thread);
			inds[start + i] = coopPop.getIndividual(indIndex);
		}

		return n;
	}

	// Select an individual from the population of cooperative entities.
	protected int getIndividualFromCoopPopulation(final EvolutionState state, final int thread) {
		if (((MLSEvolutionState) state).getCoopPopulation().getNumGroups() != 0) {
			return getGroupedIndividual(state, thread);
		} else {
			return getUngroupedIndividual(state, thread);
		}
	}

	// Select an individual from any group in the population.
	protected int getGroupedIndividual(final EvolutionState state, final int thread) {
		int groupIndex = getGroup(state, thread);

		// There is a very small possibility that the group consists of indivduals which
		// have been removed from the population. In such case, the method will return
		// NOT_SELECTED as there are no valid individuals to select from.
		// If NOT_SELECTED is indeed returned, then re-roll and hope that you don't get
		// same group again.
		int index;
		while ((index = getGroupedIndividual(state, thread, groupIndex)) == NOT_SELECTED) {
			groupIndex = getGroup(state, thread);
		}

		return index;
	}

	// Select an individual from the specified group.
	protected int getGroupedIndividual(final EvolutionState state, final int thread, final int groupIndex) {
		MLSCoopPopulation coopPop = ((MLSEvolutionState) state).getCoopPopulation();
		MLSSubpopulation group = coopPop.getGroup(groupIndex);

		// Randomly select an individual from the group.
		Individual[] unremovedInds = coopPop.getUnremovedIndividuals(group);

		if (unremovedInds.length == 0) {
			return NOT_SELECTED;
		}

		Individual ind = unremovedInds[state.random[thread].nextInt(unremovedInds.length)];

		for (int index = 0; index < coopPop.getNumIndividuals(); index++) {
			if (coopPop.getIndividual(index) == ind) {
				return index;
			}
		}

		state.output.fatal("The individual does not exist in the population of cooperative entities.");

		return NOT_SELECTED; // Never reaches this point.
	}

	// Select a group to select from.
	protected int getGroup(final EvolutionState state, final int thread) {
		MLSCoopPopulation coopPop = ((MLSEvolutionState) state).getCoopPopulation();

		double[] stackedGroupFitnesses = new double[coopPop.getNumGroups()];
		for (int i = 0; i < coopPop.getNumGroups(); i++) {
			stackedGroupFitnesses[i] = coopPop.getGroup(i).getFitness().fitness();
		}

		// Randomly choose a group proportionate to its fitness.
		return fitnessProportionate(stackedGroupFitnesses, coopPop.getNumGroups(), state.random[thread]);
	}

	// Select an individual from the lowest level, since there is no group to select from.
	protected int getUngroupedIndividual(final EvolutionState state, final int thread) {
		MLSCoopPopulation coopPop = ((MLSEvolutionState) state).getCoopPopulation();

		int size = getTournamentSizeToUse(state.random[thread]);

		int best = state.random[thread].nextInt(coopPop.getNumIndividuals());
		for (int i = 1; i < size; i++) {
			int j = state.random[thread].nextInt(coopPop.getNumIndividuals());

			if (coopPop.getIndividual(j).fitness.betterThan(coopPop.getIndividual(best).fitness)) {
				best = j;
			}
		}

		return best;
	}

	protected final int fitnessProportionate(double[] stackedFitnesses, int length, MersenneTwisterFast random) {
		double prob = random.nextDouble();

		double[] stackedProb = new double[length];
		for (int i = 0; i < length; i++) {
			stackedProb[i] = stackedFitnesses[i] / stackedFitnesses[stackedFitnesses.length - 1];
		}

		return RandomChoice.pickFromDistribution(stackedProb, prob);
	}

}
