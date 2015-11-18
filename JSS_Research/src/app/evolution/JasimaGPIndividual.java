package app.evolution;

import ec.Fitness;
import ec.gp.GPIndividual;

public class JasimaGPIndividual extends GPIndividual implements JasimaReproducible {

	private static final long serialVersionUID = -44718398627919441L;

	public Fitness getFitness() {
		return fitness;
	}

}
