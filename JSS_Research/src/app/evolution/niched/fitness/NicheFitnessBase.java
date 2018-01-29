package app.evolution.niched.fitness;

import app.evolution.JasimaFitnessBase;
import app.evolution.JasimaGPIndividual;
import app.evolution.niched.JasimaNichedIndividual;
import app.simConfig.SimConfig;
import ec.EvolutionState;

public abstract class NicheFitnessBase extends JasimaFitnessBase<JasimaGPIndividual> {

	public abstract void init(final EvolutionState state, final SimConfig config, final int threadnum);

	public abstract int getNumNiches(final SimConfig config);

	public abstract void updateNiches(final EvolutionState state, final SimConfig config, final JasimaNichedIndividual reproducible);

	@Override
	public void setFitness(final EvolutionState state, final SimConfig config, final JasimaGPIndividual reproducible) {
		super.setFitness(state, config, reproducible);

		// Update the niched individuals if necessary.
		updateNiches(state, config, (JasimaNichedIndividual) reproducible);
	}

	public void setNichedFitness(final EvolutionState state,
			final int nicheIndex,
			final SimConfig config,
			final JasimaNichedIndividual reproducible) {
		double finalFitness = getFinalFitness(state, config, reproducible);

		reproducible.setNichedFitness(nicheIndex, finalFitness);
	}

	public void updateArchive(final EvolutionState state,
			final JasimaNichedIndividual[] nichedInds,
			final int threadnum) {
		for (int i = 0; i < state.population.archive.length; i++) {
			// The individual's in the current generation archive have already been evaluated in the
			// niche specific training set, so use the fitnesses from those.
			JasimaNichedIndividual archiveInd = (JasimaNichedIndividual) state.population.archive[i];

			if (archiveInd == null || nichedInds[i].getNichedFitness(i) < archiveInd.getNichedFitness(i)) {
				state.population.archive[i] = nichedInds[i];
			}
		}
	}

}
