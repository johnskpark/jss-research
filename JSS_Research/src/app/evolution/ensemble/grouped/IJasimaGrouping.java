package app.evolution.ensemble.grouped;

import ec.EvolutionState;
import ec.Individual;
import ec.Setup;
import ec.gp.koza.KozaFitness;

public interface IJasimaGrouping extends Setup {

	public void groupIndividuals(final EvolutionState state, final int threadnum);

	public int getGroupSize();

	public boolean isIndEvaluated();

	public boolean isGroupEvaluated();

	public GroupedIndividual getGroups(final Individual ind);

	public GroupedIndividual getBestGroup();

	public KozaFitness getBestGroupFitness();

	public GroupedIndividual getBestGroupForGeneration();

	public KozaFitness getBestGroupForGenerationFitness();

	public void updateFitness(final EvolutionState state,
			final GroupedIndividual indGroup,
			final double fitness);

	public void clearForGeneration(final EvolutionState state);

}
