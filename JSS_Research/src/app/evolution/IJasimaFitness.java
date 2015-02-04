package app.evolution;

import jasima.core.statistics.SummaryStat;

import java.util.Map;

import ec.EvolutionState;
import ec.Individual;

public interface IJasimaFitness {

	public void accumulateFitness(final Map<String, Object> results);

	public void accumulateTrackerFitness(final SummaryStat trackerStat);

	public void setFitness(final EvolutionState state, final Individual ind);

	public void clear();

}
