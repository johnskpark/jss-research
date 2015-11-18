package app.evolution.grouped;

import java.util.Map;

import app.evolution.IJasimaFitness;
import app.evolution.JasimaGPIndividual;
import ec.EvolutionState;
import ec.Individual;

public interface IJasimaGroupFitness extends IJasimaFitness<JasimaGPIndividual> {

	public void accumulateIndFitness(final Individual ind, final Map<String, Object> results);

	public void accumulateGroupFitness(final Individual ind,
			final Map<String, Object> results);

	public void setIndFitness(final EvolutionState state, final Individual ind);

	public void setGroupFitness(final EvolutionState state,
			final Individual ind,
			final GroupedIndividual group);

	public void clearIndFitness();

	public void clearGroupFitness();

}
