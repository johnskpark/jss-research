package ec.multilevel_new;

import java.util.ArrayList;
import java.util.List;

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
		// Use a list to remove duplicates. Sets cannot be used, as memory blocks
		// are used to represent individuals in a set, and therefore is not consistent.
		List<Individual> indList = new ArrayList<Individual>(coop1.getIndividuals().length + coop2.getIndividuals().length);
		for (Individual ind : coop1.getIndividuals()) {
			if (!indList.contains(ind)) { indList.add(ind); }
		}
		for (Individual ind : coop2.getIndividuals()) {
			if (!indList.contains(ind)) { indList.add(ind); }
		}

		// Simply clone the first subpopulation in the population for the new group.
		MLSSubpopulation subpop = (MLSSubpopulation) state.population.subpops[0].emptyClone();
		subpop.individuals = new Individual[indList.size()];

		// Copy the individuals from the set into the subpopulation.
		indList.toArray(subpop.individuals);

		return subpop;
	}

}
