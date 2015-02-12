package app.evolution;

import jasima.core.util.Pair;

import java.util.Map;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;

public interface IJasimaGroupFitness {

	public void accumulateIndFitness(Individual ind, final Map<String, Object> results);

	public void accumulateGroupFitness(final Pair<GPIndividual, Double>[] groupResults);

	public void setIndFitness(final EvolutionState state, Individual ind);

	public void setGroupFitness(final EvolutionState state, GPIndividual[] inds);

	public void clearIndFitness();

	public void clearGroupFitness();

	public void clear();

}
