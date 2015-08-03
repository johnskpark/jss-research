package app.evolution.multilevel;

import ec.EvolutionState;
import ec.Fitness;
import ec.Group;
import ec.Subpopulation;
import ec.util.Parameter;

public class MLSSubpopulation extends Subpopulation {

	public static final String P_FITNESS = "fitness";

	private Fitness fitness;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		Parameter def = defaultBase();

		fitness = (Fitness) state.parameters.getInstanceForParameter(base.push(P_FITNESS),
				def.push(P_FITNESS),
				Fitness.class);
	}

	@Override
	public Group emptyClone() {
		// TODO this is ambiguous, might need to fix sometime later down the line.
		MLSSubpopulation clone = (MLSSubpopulation) super.emptyClone();
		clone.fitness = fitness;
		return clone;
	}

	public Fitness getFitness() {
		return fitness;
	}

}
