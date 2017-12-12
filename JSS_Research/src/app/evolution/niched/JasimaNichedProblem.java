package app.evolution.niched;

import app.evolution.IJasimaFitness;
import app.evolution.JasimaGPIndividual;
import app.evolution.niched.fitness.NicheFitness;
import app.evolution.simple.JasimaSimpleProblem;
import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;
import jasima.core.experiment.Experiment;

public class JasimaNichedProblem extends JasimaSimpleProblem {

	private static final long serialVersionUID = -3573529649173003108L;

	private static final int NOT_SET = -1;

	// TODO the code here.

	private int numNiches = NOT_SET;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		if (!(getFitness() instanceof NicheFitness)) {
			state.output.fatal("The fitness must be of type NicheFitness.");
		}

		NicheFitness nicheFitness = (NicheFitness) getFitness();
		numNiches = nicheFitness.getNumNiches(getSimConfig());

		state.population.archive = new JasimaNichedIndividual[numNiches];
	}

	@Override
	public void prepareToEvaluate(final EvolutionState state, final int threadnum) {
		super.prepareToEvaluate(state, threadnum);

		NicheFitness nicheFitness = (NicheFitness) getFitness();
		nicheFitness.init(state, getSimConfig(), threadnum);
	}

	@Override
	public void finishEvaluating(final EvolutionState state, final int threadnum) {
		super.finishEvaluating(state, threadnum);

		NicheFitness nicheFitness = (NicheFitness) getFitness();
		JasimaGPIndividual[] nichedInds = nicheFitness.getNichedIndividuals();

		// Update the nicheFitnesses of the current generation niched individuals.
		for (int i = 0; i < nichedInds.length; i++) {
			// TODO run more experiments using the simulator.
		}

		// Update the overall archive of overall niched individuals.
		nicheFitness.updateArchive(state, getSimConfig(), threadnum);

		// Update the fitnesses of the individuals using the niched individuals.
		for (int i = 0; i < state.population.subpops.length; i++) {
			for (int j = 0; j < state.population.subpops[j].individuals.length; j++) {
				// TODO this will have to be done in the problem class.
			}
		}
	}


	@Override
	public void evaluate(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		if (!ind.evaluated) {
			configureRule(state,
					getRule(),
					getTracker(),
					new Individual[] {ind},
					new int[] {subpopulation},
					threadnum);
			initialiseTracker(getTracker());

			for (int i = 0; i < getSimConfig().getNumConfigs(); i++) {
				Experiment experiment = getExperiment(state, getRule(), i, getWorkStationListeners(), getTracker());
				experiment.runExperiment();

				getFitness().accumulateFitness(i, getSimConfig(), (JasimaGPIndividual) ind, experiment.getResults());

				clearForExperiment(getWorkStationListeners());
			}

			getFitness().setFitness(state, getSimConfig(), (JasimaGPIndividual) ind);
			getFitness().clear();

			ind.evaluated = true;

			clearForRun(getTracker());
		}
	}

	@Override
	public Object clone() {
		JasimaNichedProblem newObject = (JasimaNichedProblem) super.clone();

		return newObject;
	}

}
