package app.evolution.grouped;

import jasima.core.util.Pair;

import java.util.Map;

import app.evolution.IJasimaFitness;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;

public interface IJasimaGroupFitness extends IJasimaFitness {

	public void accumulateIndFitness(final Individual ind, final Map<String, Object> results);

	public void accumulateGroupFitness(final Pair<GPIndividual, Double>[] groupResults);

	public void setIndFitness(final EvolutionState state, final Individual ind);

	public void setGroupFitness(final EvolutionState state, final GPIndividual[] inds);

	public void clearIndFitness();

	public void clearGroupFitness();

}
