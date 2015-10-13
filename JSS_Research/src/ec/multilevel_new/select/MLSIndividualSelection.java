package ec.multilevel_new.select;

import ec.EvolutionState;
import ec.Individual;
import ec.SelectionMethod;
import ec.multilevel_new.MLSCoopPopulation;
import ec.multilevel_new.MLSEvolutionState;
import ec.multilevel_new.MLSGPIndividual;
import ec.multilevel_new.MLSSubpopulation;
import ec.select.SelectDefaults;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;

// TODO this will need to select based on the fitness of the groups.
public class MLSIndividualSelection extends SelectionMethod {

	public static final String P_MULTILEVEL = "multilevel";
    public static final String P_SIZE = "size";

	public static final int INDS_SELECTED = 1;

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
		MLSCoopPopulation coopPop = ((MLSEvolutionState) state).getCoopPopulation();

		if (coopPop.getNumGroups() == 0) {
			// If there's no groups, then apply tournament selection.

			// TODO
		} else {
			// Otherwise, select a group from fitness proportionate selection.
			// TODO
		}

		return 0;
	}

	@Override
	public int produce(final int min,
			final int max,
			final int start,
			final int subpopulation,
			final Individual[] inds,
			final EvolutionState state,
			final int thread) {
		MLSCoopPopulation coopPop = ((MLSEvolutionState) state).getCoopPopulation();

		if (coopPop.getNumGroups() == 0) {
			// If there's no groups, then apply tournament selection.
			int size = getTournamentSizeToUse(state.random[thread]);


			// TODO
		} else {
			// Otherwise, select a group from fitness proportionate selection.
			// TODO
		}

		return 0;
	}

	protected final int getIndividualFromCoopPopulation(final EvolutionState state, final int thread) {
		MLSCoopPopulation coopPop = ((MLSEvolutionState) state).getCoopPopulation();

		int index;

		if (coopPop.getNumGroups() == 0) {
			// If there's no groups, then apply tournament selection.
			int size = getTournamentSizeToUse(state.random[thread]);

			MLSGPIndividual[] inds = coopPop.getIndividuals();

			index = state.random[thread].nextInt(coopPop.getNumIndividuals());
			for (int i = 1; i < size; i++) {
				int j = state.random[thread].nextInt(coopPop.getNumIndividuals());

				if (inds[j].fitness.betterThan(inds[index].fitness)) {
					index = j;
				}
			}
		} else {
			// Otherwise, select a group from fitness proportionate selection.
			MLSSubpopulation[] groups = coopPop.getGroups();

			double[] stackedGroupFitnesses = new double[coopPop.getNumGroups()];
			for (int i = 0; i < coopPop.getNumGroups(); i++) {
				stackedGroupFitnesses[i] = groups[i].getFitness().fitness();
			}

			// TODO select
			int groupIndex = rouletteWheel(stackedGroupFitnesses, coopPop.getNumGroups(), state.random[thread]);

			index = 0;
		}

		return index;
	}

	protected final int rouletteWheel(double[] stackedFitnesses, int length, MersenneTwisterFast random) {
		double prob = random.nextDouble();

		double[] stackedProb = new double[length];
		for (int i = 0; i < length; i++) {
			stackedProb[i] = stackedFitnesses[i] / stackedFitnesses[stackedFitnesses.length - 1];
		}

		return 0;
	}

}
