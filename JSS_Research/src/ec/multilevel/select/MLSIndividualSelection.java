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
		int groupIndex = getValidGroup(state, thread);

		// There is a very small possibility that the group consists of indivduals which
		// have been removed from the population. In such case, the method will return
		// NOT_SELECTED as there are no valid individuals to select from.
		// If NOT_SELECTED is indeed returned, then re-roll and hope that you don't get
		// same group again.
		// TODO old code for when you need to cycle between the groups.
//		int index;
//		while ((index = getGroupedIndividual(state, thread, groupIndex)) == NOT_SELECTED) {
//			groupIndex = getGroup(state, thread);
//		}
//
//		return index;
		
		int index = getGroupedIndividual(state, thread, groupIndex);
		if (index == NOT_SELECTED) {
			state.output.fatal("The individual selected from the group " + groupIndex + " does not belong to the population.");
		}
		
		return index;
	}

	// Select an individual from the specified group.
	protected int getGroupedIndividual(final EvolutionState state, final int thread, final int groupIndex) {
		MLSCoopPopulation coopPop = ((MLSEvolutionState) state).getCoopPopulation();
		MLSSubpopulation group = coopPop.getValidGroup(groupIndex);

		// Randomly select an individual from the group.
		Individual[] unremovedInds = coopPop.getUnremovedIndividuals(group);

		if (unremovedInds.length == 0) {
			state.output.fatal("Group " + groupIndex + " only contains individuals removed from the population. No individuals selected.");
		}

		int indIndex = state.random[thread].nextInt(unremovedInds.length);
		Individual ind = unremovedInds[indIndex];

		for (int index = 0; index < coopPop.getNumIndividuals(); index++) {
			if (ind.equals(coopPop.getIndividual(index))) {
				return index;
			}
		}

		state.output.fatal("The individual does not exist in the population of cooperative entities. Group index: " + groupIndex + ", Individual index in group: " + indIndex);

		return NOT_SELECTED; // Never reaches this point.
	}

	// Select a group to select an individual from.
	protected int getGroup(final EvolutionState state, final int thread) {
		MLSCoopPopulation coopPop = ((MLSEvolutionState) state).getCoopPopulation();

		double[] stackedGroupFitnesses = new double[coopPop.getNumGroups()];
		stackedGroupFitnesses[0] = coopPop.getGroup(0).getFitness().fitness();
		for (int i = 1; i < coopPop.getNumGroups(); i++) {
			stackedGroupFitnesses[i] = stackedGroupFitnesses[i-1] + coopPop.getGroup(i).getFitness().fitness();
		}

		// Randomly choose a group proportionate to its fitness.
		return fitnessProportionate(stackedGroupFitnesses, coopPop.getNumGroups(), state.random[thread]);
	}
	
	// Select a group to select an individual from.
	protected int getValidGroup(final EvolutionState state, final int thread) {
		MLSCoopPopulation coopPop = ((MLSEvolutionState) state).getCoopPopulation();
		MLSSubpopulation[] groups = coopPop.getValidGroups();
		
		if (groups.length == 0) {
			state.output.fatal("There are no groups containing at least one individual from the population. There is something going wrong here.");
		}
		
		double[] stackedGroupFitnesses = new double[groups.length];
		stackedGroupFitnesses[0] = groups[0].getFitness().fitness();
		for (int i = 1; i < groups.length; i++) {
			stackedGroupFitnesses[i] = stackedGroupFitnesses[i-1] + groups[i].getFitness().fitness();
		}

		// Randomly choose a group proportionate to its fitness.
		return fitnessProportionate(stackedGroupFitnesses, groups.length, state.random[thread]);
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
