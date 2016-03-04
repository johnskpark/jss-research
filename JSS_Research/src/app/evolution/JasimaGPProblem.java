package app.evolution;

import java.util.ArrayList;
import java.util.List;

import app.IWorkStationListener;
import app.priorityRules.HolthausRule;
import app.simConfig.DynamicSimConfig;
import app.simConfig.ExperimentGenerator;
import app.tracker.JasimaEvolveExperimentTracker;
import ec.EvolutionState;
import ec.gp.GPProblem;
import ec.util.MersenneTwisterFast;
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
	public static final String P_SEED = "seed";

	public static final String P_TRACKER = "tracker";

	public static final String P_WORKSTATION = "workstation";

	public static final String P_REFERENCE_RULE = "reference-rule";

	public static final String P_ROTATE_SEED = "rotate-seed";

	public static final long DEFAULT_SEED = 15;

	private boolean shouldSetContext;

	private DynamicSimConfig simConfig;
	private long simSeed;
	private MersenneTwisterFast rand;

	private JasimaEvolveExperimentTracker experimentTracker;

	private IWorkStationListener workstationListener;

	private PR referenceRule = new HolthausRule();
	private List<Double> referenceInstStats = new ArrayList<Double>();

	private boolean rotateSeed;

	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// Load whether we should set context or not.
		shouldSetContext = state.parameters.getBoolean(base.push(P_SHOULD_SET_CONTEXT), null, true);

		// Setup the GPData.
		input = (JasimaGPData) state.parameters.getInstanceForParameterEq(base.push(P_DATA), null, JasimaGPData.class);
		input.setup(state, base.push(P_DATA));

		// Setup the simulator configurations.
		simConfig = (DynamicSimConfig) state.parameters.getInstanceForParameterEq(base.push(P_SIMULATOR), null, DynamicSimConfig.class);
		simSeed = state.parameters.getLongWithDefault(base.push(P_SIMULATOR).push(P_SEED), null, DEFAULT_SEED);
		rand = new MersenneTwisterFast(simSeed);

		simConfig.setSeed(simSeed);

		rotateSeed = state.parameters.getBoolean(base.push(P_ROTATE_SEED), null, true);

		state.output.message("JasimaGPProblem rotate seed: " + rotateSeed);

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
        } catch (ParamClassLoadException ex) {
        	state.output.warning("No reference rule provided for JasimaGPProblem.");
        }
	}

	protected boolean shouldSetContext() {
		return shouldSetContext;
	}

	public DynamicSimConfig getSimConfig() {
		return simConfig;
	}

	protected long getSimSeed() {
		return simSeed;
	}

	protected void rotateSimSeed() {
		if (rotateSeed) {
			simConfig.setSeed(rand.nextLong());
		}
	}

	protected void resetSimSeed() {
		simConfig.resetSeed();
	}

	protected MersenneTwisterFast getRandom() {
		return rand;
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

	protected boolean hasReferenceRule() {
		return referenceRule != null;
	}

	protected PR getReferenceRule() {
		return referenceRule;
	}

	protected List<Double> getReferenceStat() {
		if (referenceRule == null) {
			throw new RuntimeException("getReferenceStat(): Reference rule is not initialised.");
		}

		return referenceInstStats;
	}

	protected void evaluateReference() {
		if (!hasReferenceRule()) {
			throw new RuntimeException("evaluateReference(): Reference rule is not initialised.");
		}
		if (referenceInstStats.size() != 0) {
			throw new RuntimeException("The reference rule has been previously evaluated. Please clear the statistics for the reference rule beforehand.");
		}

		for (int expIndex = 0; expIndex < getSimConfig().getNumConfigs(); expIndex++) {
			Experiment experiment = ExperimentGenerator.getExperiment(simConfig,
					referenceRule,
					expIndex);

			experiment.runExperiment();

			SummaryStat stat = (SummaryStat) experiment.getResults().get("weightedTardMean");
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

		newObject.simConfig = simConfig;
		newObject.simSeed = simSeed;
		newObject.rand = (MersenneTwisterFast) rand.clone();

		newObject.experimentTracker = experimentTracker;

		newObject.workstationListener = workstationListener;
		((JasimaGPData) newObject.input).setWorkStationListener(newObject.workstationListener);

		return newObject;
	}

}
