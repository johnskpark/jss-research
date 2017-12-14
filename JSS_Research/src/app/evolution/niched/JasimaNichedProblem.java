package app.evolution.niched;

import app.evolution.ISimConfigEvolveFactory;
import app.evolution.JasimaGPIndividual;
import app.evolution.niched.fitness.NicheFitness;
import app.evolution.simple.JasimaSimpleProblem;
import app.simConfig.SimConfig;
import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;
import jasima.core.experiment.Experiment;

public class JasimaNichedProblem extends JasimaSimpleProblem {

	private static final long serialVersionUID = -3573529649173003108L;

	public static final String P_NICHE = "niche";

	private static final int NOT_SET = -1;

	private int numNiches = NOT_SET;

	private ISimConfigEvolveFactory[] nicheSimConfigFactories;
	private SimConfig[] nicheSimConfigs;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		if (!(getFitness() instanceof NicheFitness)) {
			state.output.fatal("The fitness must be of type NicheFitness.");
		}

		NicheFitness nicheFitness = (NicheFitness) getFitness();
		numNiches = nicheFitness.getNumNiches(getSimConfig());

		state.population.archive = new JasimaNichedIndividual[numNiches];

		nicheSimConfigs = new SimConfig[numNiches];

		for (int i = 0; i < numNiches; i++) {
			Parameter nicheParam = base.push(P_NICHE).push(i+"");

			// Setup the simulator configurations.
			// It will look something like "eval.problem.niche.0.simulator = ..."
			nicheSimConfigFactories[i] = (ISimConfigEvolveFactory) state.parameters.getInstanceForParameterEq(nicheParam.push(P_SIMULATOR), null, ISimConfigEvolveFactory.class);
			nicheSimConfigFactories[i].setup(state, nicheParam.push(P_SIMULATOR));
			nicheSimConfigs[i] = nicheSimConfigFactories[i].generateSimConfig();
		}
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
			evaluateNiched(state, nichedInds[i], threadnum);
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

	public void evaluateNiched(final EvolutionState state,
			final JasimaGPIndividual ind,
			final int threadnum) {
		if (!(ind instanceof JasimaNichedIndividual)) {
			state.output.fatal("The niche individual must be of type JasimaNichedIndividual");
		}

		NicheFitness fitness = (NicheFitness) getFitness();

		// TODO
	}

	@Override
	public Object clone() {
		JasimaNichedProblem newObject = (JasimaNichedProblem) super.clone();

		return newObject;
	}

}
