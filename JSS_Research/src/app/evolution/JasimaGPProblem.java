package app.evolution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.IWorkStationListener;
import app.priorityRules.HolthausRule;
import app.simConfig.ExperimentGenerator;
import app.simConfig.SimConfig;
import app.stat.WeightedTardinessStat;
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

	private boolean shouldSetContext;

	private ISimConfigEvolveFactory simConfigFactory;
	private SimConfig simConfig;

	private JasimaExperimentTracker<AbsGPPriorityRule> experimentTracker;

	private Map<String, IWorkStationListener> workstationListeners = new HashMap<String, IWorkStationListener>();

	private PR referenceRule = new HolthausRule();
	private List<Double> referenceInstStats = new ArrayList<Double>();

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

		state.output.message("JasimaGPProblem rotate seed: " + simConfigFactory.rotatesSeed());

		// Setup the tracker.
		try {
			experimentTracker = (JasimaExperimentTracker<AbsGPPriorityRule>) state.parameters.getInstanceForParameterEq(base.push(P_TRACKER), null, JasimaExperimentTracker.class);
			experimentTracker.setSimConfig(simConfig);
		} catch (ParamClassLoadException ex) {
			state.output.warning("No tracker provided for JasimaGPProblem.");
		}

        // Setup the workstation listener.
        try {
        	int numWorkstations = state.parameters.getInt(base.push(P_NUM_WORKSTATIONS), null, 0);

        	for (int i = 0; i < numWorkstations; i++) {
        		Parameter workstationParam = base.push(P_WORKSTATION).push(i+"");
	        	IWorkStationListenerEvolveFactory factory = (IWorkStationListenerEvolveFactory)
	        			state.parameters.getInstanceForParameterEq(workstationParam, null, IWorkStationListenerEvolveFactory.class);
	        	factory.setup(state, workstationParam);

	        	IWorkStationListener listener = factory.generateWorkStationListener();
	        	String listenerName = listener.getClass().getSimpleName();

	        	workstationListeners.put(listenerName, listener);
        	}

    		// Feed in the shop simulation listener to input.
            ((JasimaGPData) input).setWorkStationListener(workstationListeners);
        } catch (ParamClassLoadException ex) {
        	state.output.warning("No workstation listener provided for JasimaGPProblem.");
        }

        // Setup the reference rule.
        try {
        	referenceRule = (PR) state.parameters.getInstanceForParameterEq(base.push(P_REFERENCE_RULE), null, PR.class);

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

	protected JasimaExperimentTracker<AbsGPPriorityRule> getTracker() {
		return experimentTracker;
	}

	protected boolean hasWorkStationListener() {
		return !workstationListeners.isEmpty();
	}

	protected Map<String, IWorkStationListener> getWorkStationListeners() {
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
			AbsGPPriorityRule rule) {
		// Reset the seed for the simulator.
		rotateSimSeed();

		// Setup the tracker.
		if (hasTracker()) {
			getTracker().addPriorityRule(rule);
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
			AbsGPPriorityRule rule) {
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

			// FIXME This part is hard coded, so fix this part in some future date.
			double twt = WeightedTardinessStat.getTotalWeightedTardiness(experiment.getResults());
			referenceInstStats.add(twt);
		}

		resetSimSeed();
	}

	protected void clearReference() {
		referenceInstStats.clear();
	}

	protected void configureRule(final EvolutionState state,
			final AbsGPPriorityRule rule,
			final JasimaExperimentTracker<AbsGPPriorityRule> tracker,
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

	protected void initialiseTracker(JasimaExperimentTracker<AbsGPPriorityRule> tracker) {
		if (tracker != null) {
			tracker.initialise();
		}
	}

	protected void clearForExperiment(Map<String, IWorkStationListener> listeners) {
		if (hasWorkStationListener()) {
			for (IWorkStationListener listener : listeners.values()) {
				listener.clear();
			}
		}
	}

	protected void clearForRun(JasimaExperimentTracker<AbsGPPriorityRule> tracker) {
		if (tracker != null) {
			tracker.clear();
		}

		resetSimSeed();
	}

	protected Experiment getExperiment(final EvolutionState state,
			final AbsGPPriorityRule rule,
			final int index,
			final Map<String, IWorkStationListener> listeners,
			final JasimaExperimentTracker<AbsGPPriorityRule> tracker) {
		JobShopExperiment experiment = ExperimentGenerator.getExperiment(simConfig,
				rule,
				index);

		// Add the workstation listener.
		experiment.addMachineListener(rule);
		if (hasWorkStationListener()) {
			for (IWorkStationListener listener : listeners.values()) {
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

		newObject.workstationListeners = new HashMap<String, IWorkStationListener>(workstationListeners);
		((JasimaGPData) newObject.input).setWorkStationListener(newObject.workstationListeners);

		return newObject;
	}

}
