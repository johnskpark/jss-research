package ec.multilevel_new;

import ec.EvolutionState;
import ec.Population;
import ec.Subpopulation;
import ec.util.Checkpoint;
import ec.util.Parameter;

/**
 * TODO javadoc.
 *
 * Needs same subpopulation size for all subpopulation.
 *
 * @author parkjohn
 *
 */

// TODO right, I need to make it as follows:
// - If the number of subpopulations is 1, then there is no "group".
// - If the number of subpopulations is greater than 1, then each subpopulation forms a "group".
public class MLSEvolutionState extends EvolutionState {

	private static final long serialVersionUID = -1318016754719247209L;

	// Evaluator can breed the subpopulation from here.

	/**
	 * metaPopulation stores the intermediate subpopulations of individuals
	 * which are constructed for the multi-level selection procedure.
	 */
	private Population metaPopulation;

	private MLSCoopPopulation coopPopulation;;

	private int totalNumIndividuals;
	private int initNumSubpops;
	private int initSubpopSize;

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
		// TODO Right, I need to fix this.

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

		totalNumIndividuals = 0;
		initNumSubpops = population.subpops.length;
		initSubpopSize = population.subpops[0].individuals.length;

		// FIXME Check to ensure that you start with a single population.
		if (population.subpops.length != 1) {
			output.fatal("TEMPORARY: You must start with a single population.");
		}

		// Ensure that the subpopulations are a type of MLSSubpopulation.
		for (int subpop = 0; subpop < population.subpops.length; subpop++) {
			if (!(population.subpops[subpop] instanceof MLSSubpopulation)) {
				output.fatal("Subpopulation " + subpop + " is not of type MLSSubpopulation.");
			}

			totalNumIndividuals += population.subpops[subpop].individuals.length;
		}
	}

	public void initialiseMetaPopulation(int numToBreed) {
		metaPopulation = (Population) population.emptyClone();
		metaPopulation.subpops = new Subpopulation[population.subpops.length + numToBreed];

		for (int i = 0; i < metaPopulation.subpops.length; i++) {
			// The MLSBreeder will generate the appropriate individuals array size for the meta-population,
			// so simply use the first subpopulation to clone (for now).
			metaPopulation.subpops[i] = (Subpopulation) population.subpops[0].emptyClone();
		}
	}

	@Override
	public int evolve() {
		if (generation > 0) {
			output.message("Generation " + generation);
		}

		// Evaluate the newly generated population.
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

		// Pre-breeding exchange is not called for MLSEvolutionState (for now).

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

	@Override
	public void finish(int result) {
        statistics.finalStatistics(this,result);
        finisher.finishPopulation(this,result);
        exchanger.closeContacts(this,result);
        evaluator.closeContacts(this,result);
	}

	/**
	 * Returns the reference to the meta population.
	 */
	public Population getMetaPopulation() {
		return metaPopulation;
	}

	public MLSCoopPopulation getCoopPopulation() {
		return coopPopulation;
	}

	public void setCoopPopulation(MLSCoopPopulation coopPopulation) {
		this.coopPopulation = coopPopulation;
	}

}
