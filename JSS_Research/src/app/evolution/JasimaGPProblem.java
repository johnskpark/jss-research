package app.evolution;

import java.util.ArrayList;
import java.util.List;

import app.IWorkStationListener;
import app.priorityRules.HolthausRule;
import app.simConfig.AbsSimConfig;
import app.tracker.JasimaEvolveExperimentTracker;
import ec.EvolutionState;
import ec.gp.GPProblem;
import ec.util.MersenneTwisterFast;
import ec.util.ParamClassLoadException;
import ec.util.Parameter;
import jasima.core.statistics.SummaryStat;
import jasima.core.util.observer.NotifierListener;
import jasima.shopSim.core.PR;
import jasima.shopSim.models.dynamicShop.DynamicShopExperiment;
import jasima.shopSim.util.BasicJobStatCollector;

public abstract class JasimaGPProblem extends GPProblem {

	private static final long serialVersionUID = 5563220542866613259L;

	public static final String P_SHOULD_SET_CONTEXT = "set-context";

	public static final String P_SIMULATOR = "simulator";
	public static final String P_SEED = "seed";

	public static final String P_TRACKER = "tracker";

	public static final String P_WORKSTATION = "workstation";

	public static final long DEFAULT_SEED = 15;

	private boolean shouldSetContext;

	private AbsSimConfig simConfig;
	private long simSeed;
	private MersenneTwisterFast rand;

	private JasimaEvolveExperimentTracker experimentTracker;

	private IWorkStationListener workstationListener;

	private PR referenceRule = new HolthausRule();
	private List<Double> referenceStat = new ArrayList<Double>();

	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// Load whether we should set context or not.
		shouldSetContext = state.parameters.getBoolean(base.push(P_SHOULD_SET_CONTEXT), null, true);

		// Setup the GPData.
		input = (JasimaGPData) state.parameters.getInstanceForParameterEq(base.push(P_DATA), null, JasimaGPData.class);
		input.setup(state, base.push(P_DATA));

		// Setup the simulator configurations.
		simConfig = (AbsSimConfig) state.parameters.getInstanceForParameterEq(base.push(P_SIMULATOR), null, AbsSimConfig.class);
		simSeed = state.parameters.getLongWithDefault(base.push(P_SIMULATOR).push(P_SEED), null, DEFAULT_SEED);
		rand = new MersenneTwisterFast(simSeed);

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
        	state.output.warning("No workstation listener provided for JasimaMultilevelProblem.");
        }
	}

	protected boolean shouldSetContext() {
		return shouldSetContext;
	}

	public AbsSimConfig getSimConfig() {
		return simConfig;
	}

	protected long getSimSeed() {
		return simSeed;
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

	protected PR getReferenceRule() {
		return referenceRule;
	}

	protected List<Double> getReferenceStat() {
		return referenceStat;
	}

	protected void evaluateReference() {
		for (int expIndex = 0; expIndex < getSimConfig().getNumConfigs(); expIndex++) {
			DynamicShopExperiment experiment = getExperiment(referenceRule, expIndex);
			experiment.runExperiment();

			SummaryStat stat = (SummaryStat) experiment.getResults().get("weightedTardMean");
			referenceStat.add(stat.sum());
		}
	}

	protected void clearReference() {
		referenceStat.clear();
	}

	protected DynamicShopExperiment getExperiment(final EvolutionState state,
			final AbsGPPriorityRule rule,
			final int index,
			final IWorkStationListener listener,
			final JasimaEvolveExperimentTracker tracker) {
		DynamicShopExperiment experiment = getExperiment(rule, index);

		// Add the workstation listener.
		experiment.addMachineListener(rule);
		if (listener != null) { experiment.addMachineListener(listener); }
		if (tracker != null) {
			tracker.clearCurrentExperiment();
			tracker.setExperimentIndex(index);
		}

		return experiment;
	}

	@SuppressWarnings("unchecked")
	protected DynamicShopExperiment getExperiment(final PR rule, final int index) {
		DynamicShopExperiment experiment = new DynamicShopExperiment();

		experiment.setInitialSeed(simConfig.getLongValue());
		experiment.setNumMachines(simConfig.getNumMachines(index));
		experiment.setUtilLevel(simConfig.getUtilLevel(index));
		experiment.setDueDateFactor(simConfig.getDueDateFactor(index));
		experiment.setWeights(simConfig.getWeight(index));
		experiment.setOpProcTime(simConfig.getMinOpProc(index), simConfig.getMaxOpProc(index));
		experiment.setNumOps(simConfig.getMinNumOps(index), simConfig.getMaxNumOps(index));

		experiment.setStopAfterNumJobs(simConfig.getStopAfterNumJobs());
		experiment.setSequencingRule(rule);
		experiment.setScenario(DynamicShopExperiment.Scenario.JOB_SHOP);

		BasicJobStatCollector statCollector = new BasicJobStatCollector();
		statCollector.setIgnoreFirst(simConfig.getNumIgnore());

		experiment.setShopListener(new NotifierListener[]{statCollector});

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
