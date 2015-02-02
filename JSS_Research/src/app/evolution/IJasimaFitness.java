package app.evolution;

import java.util.Map;

import ec.EvolutionState;
import ec.Individual;

public interface IJasimaFitness {

	public void setFitness(final EvolutionState state,
			final Individual ind,
			final Map<String, Object> results);
}
