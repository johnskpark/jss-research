package app.evolution.grouped;

import java.util.Map;

import app.evolution.AbsJasimaFitness;
import app.evolution.JasimaGPIndividual;
import ec.EvolutionState;
import ec.Individual;

public abstract class JasimaGroupFitness extends AbsJasimaFitness<JasimaGPIndividual> {

	public abstract void accumulateIndFitness(final int index, final JasimaGPIndividual ind, final Map<String, Object> results);

	public abstract void accumulateGroupFitness(final int index, final JasimaGPIndividual ind, final Map<String, Object> results);

	
	public abstract void setIndFitness(final EvolutionState state, final Individual ind);

	public abstract void setGroupFitness(final EvolutionState state, final Individual ind, final GroupedIndividual group);

	
	public abstract void clearIndFitness();

	public abstract void clearGroupFitness();

}
