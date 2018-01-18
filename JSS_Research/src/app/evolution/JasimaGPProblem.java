package app.evolution;

import java.util.ArrayList;
import java.util.List;

import app.Clearable;
import app.IJasimaWorkStationListener;
import app.priorityRules.HolthausRule;
import app.simConfig.ExperimentGenerator;
import app.simConfig.SimConfig;
import app.tracker.JasimaExperimentTracker;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPProblem;
import ec.util.ParamClassLoadException;
import ec.util.Parameter;
import jasima.core.experiment.Experiment;
import jasima.shopSim.core.JobShopExperiment;
import jasima.shopSim.core.PR;

public abstract class JasimaGPProblem extends GPProblem {

	private static final long serialVersionUID = 5563220542866613259L;

	public static final String P_SHOULD_SET_CONTEXT = "set-context";

	public static final String P_SIMULATOR = "simulator";

	public static final String P_TRACKER = "tracker";

	public static final String P_NUM_WORKSTATIONS = "workstations";
	public static final String P_WORKSTATION = "workstation";

	public static final String P_REFERENCE_RULE = "reference-rule";
	public static final String P_REFERENCE_FITNESS = "reference-fitness";

	private boolean shouldSetContext;

	private ISimConfigEvolveFactory simConfigFactory;
	private SimConfig simConfig;

	private JasimaExperimentTracker<Individual> experimentTracker;

	private List<IJasimaWorkStationListener> workstationListeners = new ArrayList<>();

	private PR referenceRule = new HolthausRule();
	private List<Double> referenceInstStats = new ArrayList<Double>();
	private IJasimaFitness<JasimaGPIndividual> referenceFitness = null;

	@SuppressWarnings("unchecked")
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// Load whether we should set context or not.
		shouldSetContext = state.parameters.getBoolean(base.push(P_SHOULD_SET_CONTEXT), null, true);

		// Setup the GPData.
		input = (JasimaGPData) state.parameters.getInstanceForParameterEq(base.push(P_DATA), null, JasimaGPData.class);
		input.setup(state, base.push(P_DATA));

		// Setup the simulator configurations.
		simConfigFactory = (ISimConfigEvolveFactory) state.parameters.getInstanceForParameterEq(base.push(P_SIMULATOR), null, ISimConfigEvolveFactory.class);
		simConfigFactory.setup(state, base.push(P_SIMULATOR));
		simConfig = simConfigFactory.generateSimConfig();

		state.output.message("JasimaGPProblem rotate-seed: " + simConfigFactory.rotatesSeed());

		// Setup the tracker.
		try {
			experimentTracker = (JasimaExperimentTracker<Individual>) state.parameters.getInstanceForParameterEq(base.push(P_TRACKER), null, JasimaExperimentTracker.class);
			experimentTracker.setSimConfig(simConfig);
		} catch (ParamClassLoadException ex) {
			state.output.warning("No tracker provided for JasimaGPProblem.");
		}

        // Setup the workstation listener.
        try {
        	int numWorkstations = state.parameters.getInt(base.push(P_NUM_WORKSTATIONS), null, 0);

        	for (int i = 0; i < numWorkstations; i++) {
        		Parameter workstationParam = base.push(P_WORKSTATION).push(i+"");
	        	JasimaWorkStationListenerEvolveFactory factory = (JasimaWorkStationListenerEvolveFactory)
	        			state.parameters.getInstanceForParameterEq(workstationParam, null, JasimaWorkStationListenerEvolveFactory.class);
	        	factory.setup(state, workstationParam);

	        	IJasimaWorkStationListener listener = factory.generateWorkStationListener();

	        	workstationListeners.add(listener);
        	}

    		// Feed in the shop simulation listener to input.
            ((JasimaGPData) input).setWorkStationListener(workstationListeners);
        } catch (ParamClassLoadException ex) {
        	state.output.warning("No workstation listener provided for JasimaGPProblem.");
        }

        // Setup the reference rule.
        try {
        	referenceRule = (PR) state.parameters.getInstanceForParameterEq(base.push(P_REFERENCE_RULE), null, PR.class);
        	referenceFitness = (IJasimaFitness<JasimaGPIndividual>) state.parameters.getInstanceForParameterEq(base.push(P_REFERENCE_FITNESS), null, IJasimaFitness.class);

        	state.output.message("Reference rule provided: " + referenceRule.getClass().getSimpleName());
        } catch (ParamClassLoadException ex) {
        	state.output.warning("No reference rule provided for JasimaGPProblem.");
        }
	}

	protected boolean shouldSetContext() {
		return shouldSetContext;
	}

	public SimConfig getSimConfig() {
		return simConfig;
	}

	protected void rotateSimSeed() {
		simConfig = simConfigFactory.generateSimConfig();
	}

	protected void resetSimSeed() {
		simConfig.reset();
	}

	protected boolean hasTracker() {
		return experimentTracker != null;
	}

	protected JasimaExperimentTracker<Individual> getTracker() {
		return experimentTracker;
	}

	protected boolean hasWorkStationListener() {
		return !workstationListeners.isEmpty();
	}

	protected List<IJasimaWorkStationListener> getWorkStationListeners() {
		return workstationListeners;
	}

	public boolean hasReferenceRule() {
		return referenceRule != null;
	}

	public PR getReferenceRule() {
		return referenceRule;
	}

	public List<Double> getReferenceStat() {
		if (referenceRule == null) {
			throw new RuntimeException("Reference rule is not initialised.");
		}

		return referenceInstStats;
	}

	protected void prepareToEvaluate(final EvolutionState state,
			final int threadnum,
			GPPriorityRuleBase rule) {
		// Reset the seed for the simulator.
		rotateSimSeed();

		// Setup the tracker.
		if (hasTracker()) {
			getTracker().addRule(rule);
			getTracker().setSimConfig(simConfig);
		}

		// Apply the benchmark/reference rule to the problem instances.
		if (hasReferenceRule()) {
			clearReference();
			evaluateReference();
		}
	}

	protected void finishEvaluating(final EvolutionState state,
			final int threadnum,
			GPPriorityRuleBase rule) {
		// Is empty for now. Populate with common after evaluation procedure.
	}

	protected void evaluateReference() {
		if (!hasReferenceRule()) {
			throw new RuntimeException("Cannot evaluate reference rule. Reference rule is not initialised.");
		}
		if (referenceInstStats.size() != 0) {
			throw new RuntimeException("The reference rule has been previously evaluated. Please clear the statistics for the reference rule beforehand.");
		}

		for (int expIndex = 0; expIndex < getSimConfig().getNumConfigs(); expIndex++) {
			Experiment experiment = ExperimentGenerator.getExperiment(simConfig,
					referenceRule,
					expIndex);

			experiment.runExperiment();

			double result = referenceFitness.getFitness(expIndex, getSimConfig(), null, experiment.getResults());
			referenceInstStats.add(result);
		}

		resetSimSeed();
	}

	protected void clearReference() {
		referenceInstStats.clear();
	}

	protected void configureRule(final EvolutionState state,
			final GPPriorityRuleBase rule,
			final JasimaExperimentTracker<Individual> tracker,
			final Individual[] individuals,
			final int[] subpops,
			final int threadnum) {
		JasimaGPConfig config = new JasimaGPConfig();
		config.setState(state);
		config.setIndividuals(individuals);
		config.setSubpopulations(subpops);
		config.setThreadnum(threadnum);
		config.setData((JasimaGPData) input);
		config.setSimConfig(simConfig);

		if (tracker != null) { config.setTracker(tracker); }

		rule.setConfiguration(config);
	}

	protected void initialiseTracker(JasimaExperimentTracker<Individual> tracker) {
		if (tracker != null) {
			tracker.initialise();
		}
	}

	protected void clearForExperiment(List<IJasimaWorkStationListener> listeners) {
		if (hasWorkStationListener()) {
			for (IJasimaWorkStationListener listener : listeners) {
				listener.clear();
			}
		}
	}

	protected void clearForRun(Clearable... clearables) {
		for (Clearable clearable : clearables) {
			if (clearable != null) {
				clearable.clear();
			}
		}

		resetSimSeed();
	}

	protected Experiment getExperiment(final EvolutionState state,
			final PR rule,
			final int index,
			final SimConfig simConfig,
			final List<IJasimaWorkStationListener> listeners,
			final JasimaExperimentTracker<Individual> tracker) {
		JobShopExperiment experiment = ExperimentGenerator.getExperiment(simConfig,
				rule,
				index);

		// Add the workstation listener.
		if (hasWorkStationListener()) {
			for (IJasimaWorkStationListener listener : listeners) {
				experiment.addMachineListener(listener);
			}
		}

		if (hasTracker()) {
			tracker.clearCurrentExperiment();
			tracker.setExperimentIndex(index);
		}

		return experiment;
	}

	public Object clone() {
		JasimaGPProblem newObject = (JasimaGPProblem) super.clone();

		newObject.shouldSetContext = shouldSetContext;
		newObject.input = (JasimaGPData) input.clone();

		newObject.simConfigFactory = simConfigFactory;
		newObject.simConfig = simConfig;

		newObject.experimentTracker = experimentTracker;

		newObject.workstationListeners = new ArrayList<IJasimaWorkStationListener>(workstationListeners);
		((JasimaGPData) newObject.input).setWorkStationListener(newObject.workstationListeners);

		return newObject;
	}

}
