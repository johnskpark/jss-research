package app.evolution;

import ec.EvolutionState;
import ec.Individual;

public interface IJasimaFitness {

	public void setFitness(final EvolutionState state, final Individual ind);

	public void clear();

}
