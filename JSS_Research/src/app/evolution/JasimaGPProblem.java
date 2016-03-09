package app.evolution;

import java.util.ArrayList;
import java.util.List;

import app.IWorkStationListener;
import app.priorityRules.HolthausRule;
import app.simConfig.ExperimentGenerator;
import app.simConfig.SimConfig;
import app.tracker.JasimaEvolveExperimentTracker;
import ec.EvolutionState;
import ec.gp.GPProblem;
import ec.util.ParamClassLoadException;
import ec.util.Parameter;
import jasima.core.experiment.Experiment;
import jasima.core.statistics.SummaryStat;
import jasima.shopSim.core.JobShopExperiment;
import jasima.shopSim.core.PR;

public abstract class JasimaGPProblem extends GPProblem {

	private static final long serialVersionUID = 5563220542866613259L;

	public static final String P_SHOULD_SET_CONTEXT = "set-context";

	public static final String P_SIMULATOR = "simulator";

	public static final String P_TRACKER = "tracker";

	public static final String P_WORKSTATION = "workstation";

	public static final String P_REFERENCE_RULE = "reference-rule";

	private boolean shouldSetContext;

	private ISimConfigEvolveFactory simConfigFactory;
	private SimConfig simConfig;

	private JasimaEvolveExperimentTracker experimentTracker;

	private IWorkStationListener workstationListener;

	private PR referenceRule = new HolthausRule();
	private List<Double> referenceInstStats = new ArrayList<Double>();

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
			experimentTracker = (JasimaEvolveExperimentTracker) state.parameters.getInstanceForParameterEq(base.push(P_TRACKER), null, JasimaEvolveExperimentTracker.class);
			experimentTracker.setSimConfig(simConfig);
		} catch (ParamClassLoadException ex) {
			state.output.warning("No tracker provided for JasimaGPProblem.");
		}

        // Setup the workstation listener.
        try {
        	IWorkStationListenerEvolveFactory factory = (IWorkStationListenerEvolveFactory) state.parameters.getInstanceForParameterEq(base.push(P_WORKSTATION), null, IWorkStationListenerEvolveFactory.class);
        	factory.setup(state, base.push(P_WORKSTATION));

        	workstationListener = factory.generateWorkStationListener();

    		// Feed in the shop simulation listener to input.
            ((JasimaGPData) input).setWorkStationListener(workstationListener);
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

	protected JasimaEvolveExperimentTracker getTracker() {
		return experimentTracker;
	}

	protected boolean hasWorkStationListener() {
		return workstationListener != null;
	}

	protected IWorkStationListener getWorkStationListener() {
		return workstationListener;
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
			SummaryStat stat = (SummaryStat) experiment.getResults().get("tardiness");
			referenceInstStats.add(stat.sum());
		}

		resetSimSeed();
	}

	protected void clearReference() {
		referenceInstStats.clear();
	}

	protected Experiment getExperiment(final EvolutionState state,
			final AbsGPPriorityRule rule,
			final int index,
			final IWorkStationListener listener,
			final JasimaEvolveExperimentTracker tracker) {
		JobShopExperiment experiment = ExperimentGenerator.getExperiment(simConfig,
				rule,
				index);

		// Add the workstation listener.
		experiment.addMachineListener(rule);
		if (listener != null) { experiment.addMachineListener(listener); }
		if (tracker != null) {
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

		newObject.workstationListener = workstationListener;
		((JasimaGPData) newObject.input).setWorkStationListener(newObject.workstationListener);

		return newObject;
	}

}
