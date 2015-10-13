package ec.multilevel_new;

import java.util.ArrayList;
import java.util.List;

import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;
import ec.Subpopulation;
import ec.gp.GPIndividual;

// TODO Don't think I need to override the clone() method. Test this later down the line.
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
