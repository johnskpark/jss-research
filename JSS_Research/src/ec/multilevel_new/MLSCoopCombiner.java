package ec.multilevel_new;

import ec.EvolutionState;
import ec.Individual;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class MLSCoopCombiner {

	public static final MLSCoopCombiner COOP_COMBINER = new MLSCoopCombiner();

	private MLSCoopCombiner() {
	}

	/**
	 * Combine two cooperative components together to form a group.
	 *
	 * The combination of the two cooperative components does not automatically set the fitness of the newly
	 * generated cooperative component. This will need to be done externally.
	 */
	public IMLSCoopEntity combine(final EvolutionState state, final IMLSCoopEntity coop1, final IMLSCoopEntity coop2) {
		Individual[] inds1 = coop1.getIndividuals();
		Individual[] inds2 = coop2.getIndividuals();

		// Simply clone the first subpopulation in the population.
		MLSSubpopulation subpop = (MLSSubpopulation) state.population.subpops[0].emptyClone();
		subpop.individuals = new Individual[inds1.length + inds2.length];

		for (int i = 0; i < inds1.length; i++) { subpop.individuals[i] = inds1[i]; }
		for (int i = 0; i < inds2.length; i++) { subpop.individuals[i + inds1.length] = inds2[i]; }

		return subpop;
	}

}
