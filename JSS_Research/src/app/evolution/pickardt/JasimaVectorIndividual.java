package app.evolution.pickardt;

import app.evolution.JasimaReproducible;
import ec.Fitness;
import ec.vector.IntegerVectorIndividual;

public class JasimaVectorIndividual extends IntegerVectorIndividual implements JasimaReproducible {

	private static final long serialVersionUID = -44718398627919441L;

	@Override
	public Fitness getFitness() {
		return fitness;
	}

	@Override
	public boolean isEvaluated() {
		return evaluated;
	}

	@Override
	public void setEvaluated(boolean evaluated) {
		this.evaluated = evaluated;
	}

}
