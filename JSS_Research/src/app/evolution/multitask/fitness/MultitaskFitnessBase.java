package app.evolution.multitask.fitness;

import app.evolution.JasimaFitnessBase;
import app.evolution.JasimaGPIndividual;
import app.evolution.multitask.JasimaMultitaskIndividual;
import app.simConfig.SimConfig;
import ec.EvolutionState;
import ec.gp.koza.KozaFitness;

public abstract class MultitaskFitnessBase extends JasimaFitnessBase<JasimaGPIndividual> {

	public void setTaskFitness(final EvolutionState state,
			final int index,
			final SimConfig config,
			final JasimaGPIndividual reproducible) {
		double finalFitness = getFinalFitness(state, config, reproducible);

		JasimaMultitaskIndividual ind = (JasimaMultitaskIndividual) reproducible;
		ind.setTaskFitness(index, finalFitness);
	}

	@Override
	public void setFitness(final EvolutionState state,
			final SimConfig config,
			final JasimaGPIndividual reproducible) {
		JasimaMultitaskIndividual ind = (JasimaMultitaskIndividual) reproducible;

		// For now, use the fitness set as the average of the task fitnesses.
		double sumFitnesses = 0.0;
		int count = 0;

		for (int i = 0; i < ind.getNumTasks(); i++) {
			if (ind.getTaskFitness(i) != JasimaMultitaskIndividual.NOT_SET) {
				sumFitnesses += ind.getTaskFitness(i);
				count++;
			}
		}

		((KozaFitness) reproducible.getFitness()).setStandardizedFitness(state, (sumFitnesses / count));
	}

}
