package app.evolution.multilevel;

import ec.EvolutionState;
import ec.Population;
import ec.util.Checkpoint;
import ec.util.Parameter;

public class MultilevelSelectionEvolutionState extends EvolutionState {

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
		
	}

	public void startFresh() {
		output.message("Setting up");
		setup(this,null);  // a garbage Parameter

		// POPULATION INITIALIZATION
		output.message("Initializing Generation 0");
		statistics.preInitializationStatistics(this);
		population = initializer.initialPopulation(this, 0); // unthreaded
		statistics.postInitializationStatistics(this);

		// Compute generations from evaluations if necessary
		if (numEvaluations > UNDEFINED) {
			// compute a generation's number of individuals
			int generationSize = 0;
			for (int sub=0; sub < population.subpops.length; sub++) {
				generationSize += population.subpops[sub].individuals.length;  // so our sum total 'generationSize' will be the initial total number of individuals
			}

			if (numEvaluations < generationSize) {
				numEvaluations = generationSize;
				numGenerations = 1;
				output.warning("Using evaluations, but evaluations is less than the initial total population size (" + generationSize + ").  Setting to the populatiion size.");
			}
			else {
				if (numEvaluations % generationSize != 0) {
					output.warning("Using evaluations, but initial total population size does not divide evenly into it.  Modifying evaluations to a smaller value (" + ((numEvaluations / generationSize) * generationSize) +") which divides evenly.");  // note integer division
				}
				numGenerations = (int)(numEvaluations / generationSize);  // note integer division
				numEvaluations = numGenerations * generationSize;
			}
			output.message("Generations will be " + numGenerations);
		}

		// INITIALIZE CONTACTS -- done after initialization to allow
		// a hook for the user to do things in Initializer before
		// an attempt is made to connect to island models etc.
		exchanger.initializeContacts(this);
		evaluator.initializeContacts(this);
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

		// TODO create a meta-population which consists of the constructed groups and individuals.

		// PRE-BREEDING EXCHANGING
		statistics.prePreBreedingExchangeStatistics(this);
		population = exchanger.preBreedingExchangePopulation(this);
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

		population = breeder.breedPopulation(this);

		// POST-BREEDING EXCHANGING
		statistics.postBreedingStatistics(this);

		// POST-BREEDING EXCHANGING
		statistics.prePostBreedingExchangeStatistics(this);
		population = exchanger.postBreedingExchangePopulation(this);
		statistics.postPostBreedingExchangeStatistics(this);

		// EVALUATION
		statistics.preEvaluationStatistics(this);
		evaluator.evaluatePopulation(this);
		statistics.postEvaluationStatistics(this);

		// SELECTION
		// TODO

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

	public void finish(int result) {
        statistics.finalStatistics(this,result);
        finisher.finishPopulation(this,result);
        exchanger.closeContacts(this,result);
        evaluator.closeContacts(this,result);
	}
}
