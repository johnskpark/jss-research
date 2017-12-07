package app.evolution.niched.fitness;

import app.evolution.JasimaFitnessBase;
import app.evolution.JasimaGPIndividual;
import app.simConfig.SimConfig;
import ec.EvolutionState;

public abstract class NicheFitness extends JasimaFitnessBase<JasimaGPIndividual> {

	public abstract void init(final EvolutionState state, final SimConfig config, final int threadnum);

	public abstract void finalise(final EvolutionState state, final SimConfig config, final int threadnum);

}
