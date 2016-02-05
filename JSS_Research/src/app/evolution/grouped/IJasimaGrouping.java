package app.evolution.grouped;

import ec.EvolutionState;
import ec.Individual;
import ec.Setup;
import ec.gp.koza.KozaFitness;

public interface IJasimaGrouping extends Setup {

	public void groupIndividuals(final EvolutionState state, final int threadnum);

	public int getGroupSize();

	public int getNumTrials();

	public boolean isIndEvaluated();

	public boolean isGroupEvaluated();

	public JasimaGroupedIndividual[] getGroups(final Individual ind);

	public JasimaGroupedIndividual getBestGroup();

	public KozaFitness getBestGroupFitness();

	public JasimaGroupedIndividual getBestGroupForGeneration();

	public KozaFitness getBestGroupForGenerationFitness();

	public void updateFitness(final EvolutionState state,
			final JasimaGroupedIndividual indGroup,
			final double fitness);

	public void clearForGeneration(final EvolutionState state);

}
