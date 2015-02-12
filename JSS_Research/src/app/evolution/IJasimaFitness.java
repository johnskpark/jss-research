package app.evolution;

import jasima.core.util.Pair;

import java.util.Map;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;

public interface IJasimaFitness {

	public void accumulateFitness(final Map<String, Object> results);

	public void accumulateTrackerFitness(final Pair<GPIndividual, Double>[] trackerResults);

	public void setFitness(final EvolutionState state, final Individual ind);

	public void clearFitness();

	public void clearTrackerFitness();

}
