package app.evolution.multilevel;

import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.Subpopulation;
import ec.simple.SimpleEvolutionState;
import ec.util.Checkpoint;
import ec.util.Parameter;

// FIXME make MLSEvolutionState extend EvolutionState instead of SimpleEvolutionState.
public class MLSEvolutionState extends SimpleEvolutionState {

	// Evaluator can breed the subpopulation from here.

	/**
	 * metaPopulation stores the intermediate subpopulations of individuals
	 * which are constructed for the multi-level selection procedure.
	 */
	private Population metaPopulation;

	private int numIndividuals;

	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// Checks to make sure that the different components are capable
		// of handling multilevel selection.
		if (!(state.evaluator instanceof MLSEvaluator)) {
			output.fatal("Evaluator is not an instance of MultilevelSelectionEvaluator");
		}

		if (!(state.breeder instanceof MLSBreeder)) {
			output.fatal("Breeder is not an instance of MultilevelSelectionBreeder");
		}
	}

	public void startFresh() {
		super.startFresh();

		numIndividuals = 0;

		// Check to ensure that the subpopulation is a type of MLSSubpopulation.
		for (int subpop = 0; subpop < population.subpops.length; subpop++) {
			if (!(population.subpops[subpop] instanceof MLSSubpopulation)) {
				output.fatal("Subpopulation " + subpop + " is not of type MLSSubpopulation.");
			}

			numIndividuals += population.subpops[subpop].individuals.length;
		}

		// Specific to multilevel selection.
		metaPopulation = (Population) population.emptyClone();
		metaPopulation.subpops = new Subpopulation[population.subpops.length * 2];

		for (int i = 0; i < metaPopulation.subpops.length; i++) {
			metaPopulation.subpops[i] = (Subpopulation) population.subpops[i/2].emptyClone();
		}
	}

	// TODO need to modify this to
	// Breed first.
	// Evaluate second.
	// Select third.
	public int evolve() {
		if (generation > 0) {
			output.message("Generation " + generation);
		}

		// EVALUATION
		statistics.preEvaluationStatistics(this);
		evaluator.evaluatePopulation(this);
		statistics.postEvaluationStatistics(this);

		// SHOULD WE QUIT?
		if (evaluator.runComplete(this) && quitOnRunComplete) {
			output.message("Found Ideal Individual");
			return R_SUCCESS;
		}

		// SHOULD WE QUIT?
		if (generation == numGenerations-1) {
			return R_FAILURE;
		}

		// PRE-BREEDING EXCHANGING
		statistics.prePreBreedingExchangeStatistics(this);
		metaPopulation = exchanger.preBreedingExchangePopulation(this);
		statistics.postPreBreedingExchangeStatistics(this);

		String exchangerWantsToShutdown = exchanger.runComplete(this);
		if (exchangerWantsToShutdown!=null) {
			output.message(exchangerWantsToShutdown);
			/*
			 * Don't really know what to return here.  The only place I could
			 * find where runComplete ever returns non-null is
			 * IslandExchange.  However, that can return non-null whether or
			 * not the ideal individual was found (for example, if there was
			 * a communication error with the server).
			 *
			 * Since the original version of this code didn't care, and the
			 * result was initialized to R_SUCCESS before the while loop, I'm
			 * just going to return R_SUCCESS here.
			 */

			return R_SUCCESS;
		}

		// BREEDING
		statistics.preBreedingStatistics(this);

		metaPopulation = ((MLSBreeder) breeder).breedMetaPopulation(this, metaPopulation);

		// POST-BREEDING EXCHANGING
		statistics.postBreedingStatistics(this);

		// POST-BREEDING EXCHANGING
		statistics.prePostBreedingExchangeStatistics(this);
		metaPopulation = exchanger.postBreedingExchangePopulation(this);
		statistics.postPostBreedingExchangeStatistics(this);

		// SELECTION
		population = ((MLSBreeder) breeder).breedFinalPopulation(this, metaPopulation);

		// INCREMENT GENERATION AND CHECKPOINT
		generation++;
		if (checkpoint && generation%checkpointModulo == 0) {
			output.message("Checkpointing");
			statistics.preCheckpointStatistics(this);
			Checkpoint.setCheckpoint(this);
			statistics.postCheckpointStatistics(this);
		}

		return R_NOTDONE;
	}

	protected void breedMetaPopulation() {
		// TODO Right, need to incorporate this into the program later down the line.
	}

	protected void evaluateMetaPopulation() {
		// TODO
	}

	protected void selectMetaPopulation() {
		// TODO
	}

	@Override
	public void finish(int result) {
        statistics.finalStatistics(this,result);
        finisher.finishPopulation(this,result);
        exchanger.closeContacts(this,result);
        evaluator.closeContacts(this,result);
	}

	public int getTotalNumIndividuals() {
		return numIndividuals;
	}
}
