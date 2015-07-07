package app.evolution.ensemble.eurogp;

import jasima.core.util.Pair;

import java.util.Map;

import app.evolution.IJasimaFitness;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;

public interface EuroGPFitness extends IJasimaFitness {

	public void loadIndividuals(final Individual[] inds);

	public void accumulateObjectiveFitness(final Individual[] inds, final Map<String, Object> results);

	public void accumulateDiversityFitness(final Pair<GPIndividual, Double>[] groupResults);

	public void setTrialFitness(final EvolutionState state,
			final Individual[] inds,
			final boolean[] updateFitness,
			final boolean shouldSetContext);
	
	public void setDiversityFitness(final EvolutionState state,
			final Individual[] inds,
			final boolean[] updateFitness);
	
	public void setObjectiveFitness(final EvolutionState state,
			final Individual[] inds);

}
