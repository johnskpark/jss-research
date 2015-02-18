package app.evolution.coop;

import jasima.core.util.Pair;

import java.util.Map;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;

public interface IJasimaCoopFitness {

	public void accumulateObjectiveFitness(final Individual[] inds, final Map<String, Object> results);

	public void accumulateDiversityFitness(final Pair<GPIndividual, Double>[] groupResults);

	public void setObjectiveFitness(final EvolutionState state, final Individual ind);

	public void setDiversityFitness(final EvolutionState state, final GPIndividual[] inds);

	public void clearObjectiveFitness();

	public void clearDiversityFitness();

}
