package app.evolution.multilevel;

import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.Subpopulation;
import ec.simple.SimpleEvolutionState;
import ec.util.Checkpoint;
import ec.util.Parameter;

public class MultilevelSelectionEvolutionState extends SimpleEvolutionState {

	// Evaluator can breed the subpopulation from here.

	/**
	 * metaPopulation stores the intermediate subpopulations of individuals
	 * which are constructed for the multi-level selection procedure.
	 */
	public Population metaPopulation;

	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// Checks to make sure that the different components are capable
		// of handling multilevel selection.
		if (!(state.evaluator instanceof MultilevelSelectionEvaluator)) {
			state.output.fatal("Evaluator is not an instance of MultilevelSelectionEvaluator");
		}

		if (!(state.breeder instanceof MultilevelSelectionBreeder)) {
			state.output.fatal("Breeder is not an instance of MultilevelSelectionBreeder");
		}
	}

	public void startFresh() {
		super.startFresh();

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

		metaPopulation = ((MultilevelSelectionBreeder) breeder).breedMetaPopulation(this, metaPopulation);

		// POST-BREEDING EXCHANGING
		statistics.postBreedingStatistics(this);

		// POST-BREEDING EXCHANGING
		statistics.prePostBreedingExchangeStatistics(this);
		metaPopulation = exchanger.postBreedingExchangePopulation(this);
		statistics.postPostBreedingExchangeStatistics(this);

		// EVALUATION
		statistics.preEvaluationStatistics(this);
		evaluator.evaluatePopulation(this);
		statistics.postEvaluationStatistics(this);

		// SELECTION
		population = ((MultilevelSelectionBreeder) breeder).breedFinalPopulation(this, metaPopulation);

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
		// TODO
	}

	protected void evaluateMetaPopulation() {
		// TODO
	}

	protected void selectMetaPopulation() {
		// TODO
	}

	public void finish(int result) {
        statistics.finalStatistics(this,result);
        finisher.finishPopulation(this,result);
        exchanger.closeContacts(this,result);
        evaluator.closeContacts(this,result);
	}
}
