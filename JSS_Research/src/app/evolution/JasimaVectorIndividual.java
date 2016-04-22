package app.evolution;

import ec.Fitness;
import ec.vector.DoubleVectorIndividual;

public class JasimaVectorIndividual extends DoubleVectorIndividual implements JasimaReproducible {

	private static final long serialVersionUID = 3949387711592968369L;

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
