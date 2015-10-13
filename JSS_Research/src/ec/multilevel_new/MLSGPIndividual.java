package ec.multilevel_new;

import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;
import ec.gp.GPIndividual;

public class MLSGPIndividual extends GPIndividual implements IMLSCoopEntity {

	private static final long serialVersionUID = 8266291348787520863L;

	@Override
	public Fitness getFitness() {
		return fitness;
	}

	@Override
	public Individual[] getIndividuals() {
		return new Individual[]{this};
	}

	@Override
	public IMLSCoopEntity combine(final EvolutionState state, final IMLSCoopEntity other) {
		return MLSCoopCombiner.COOP_COMBINER.combine(state, this, other);
	}

}
