package ec.multilevel;

import ec.EvolutionState;
import ec.Fitness;
import ec.Group;
import ec.Individual;
import ec.Subpopulation;
import ec.util.Parameter;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class MLSSubpopulation extends Subpopulation {

	private static final long serialVersionUID = 1740278116450965418L;

	public static final String P_FITNESS = "fitness";

	private Fitness fitness;
	private boolean evaluated;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		Parameter def = defaultBase();

		fitness = (Fitness) state.parameters.getInstanceForParameter(base.push(P_FITNESS),
				def.push(P_FITNESS),
				Fitness.class);
		evaluated = false;
	}

	@Override
	public Group emptyClone() {
		try {
			// super.emptyClone() can't be called here.
			MLSSubpopulation p = (MLSSubpopulation)clone();

			p.species = species;
			p.individuals = new Individual[individuals.length];
			p.fitness = fitness;
			p.evaluated = false;

			return p;
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

	/**
	 * Returns the fitness of the subpopulation.
	 */
	public Fitness getFitness() {
		return fitness;
	}

	/**
	 * Returns true if the subpopulation has been evaluated.
	 */
	public boolean isEvaluated() {
		return evaluated;
	}

	/**
	 * Set whether the subpopulation it is evaluated or not.
	 */
	public void setEvaluated(boolean eval) {
		evaluated = eval;
	}

}
