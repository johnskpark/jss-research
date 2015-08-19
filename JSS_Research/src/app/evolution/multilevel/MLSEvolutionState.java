package app.evolution.multilevel;

import ec.EvolutionState;
import ec.Population;
import ec.Subpopulation;
import ec.util.Checkpoint;
import ec.util.Parameter;

public class MLSEvolutionState extends EvolutionState {

	private static final long serialVersionUID = -1318016754719247209L;

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
        output.message("Setting up");
        setup(this, null);  // a garbage Parameter

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
        	} else {
        		if (numEvaluations % generationSize != 0) {
        			output.warning("Using evaluations, but initial total population size does not divide evenly into it.  Modifying evaluations to a smaller value ("
        					+ ((numEvaluations / generationSize) * generationSize) +") which divides evenly.");  // note integer division
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
		metaPopulation.subpops = new Subpopulation[population.subpops.length*2];

		for (int i = 0; i < metaPopulation.subpops.length; i++) {
			metaPopulation.subpops[i] = (Subpopulation) population.subpops[i/2].emptyClone();
		}
	}

	@Override
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