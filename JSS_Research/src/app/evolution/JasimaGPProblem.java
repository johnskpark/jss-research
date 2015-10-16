package app.evolution;

import jasima.core.experiment.Experiment;
import jasima.core.util.observer.NotifierListener;
import jasima.shopSim.models.dynamicShop.DynamicShopExperiment;
import jasima.shopSim.util.BasicJobStatCollector;
import app.listener.IWorkStationListener;
import app.simConfig.AbsSimConfig;
import ec.EvolutionState;
import ec.gp.GPProblem;
import ec.util.MersenneTwisterFast;
import ec.util.ParamClassLoadException;
import ec.util.Parameter;

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

	private IJasimaTracker tracker;

	private IWorkStationListener workstationListener;

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
			tracker = (IJasimaTracker) state.parameters.getInstanceForParameterEq(base.push(P_TRACKER), null, IJasimaTracker.class);
			tracker.setProblem(this);
		} catch (ParamClassLoadException ex) {
			state.output.warning("No tracker provided for JasimaMultilevelProblem.");
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

	protected AbsSimConfig getSimConfig() {
		return simConfig;
	}

	protected long getSimSeed() {
		return simSeed;
	}

	protected MersenneTwisterFast getRandom() {
		return rand;
	}

	protected boolean hasTracker() {
		return tracker != null;
	}

	protected IJasimaTracker getTracker() {
		return tracker;
	}

	protected boolean hasWorkStationListener() {
		return workstationListener != null;
	}

	protected IWorkStationListener getWorkStationListener() {
		return workstationListener;
	}

	@SuppressWarnings("unchecked")
	protected Experiment getExperiment(final EvolutionState state, AbsGPPriorityRule rule, int index) {
		DynamicShopExperiment experiment = new DynamicShopExperiment();

		experiment.setInitialSeed(simConfig.getLongValue());
		experiment.setNumMachines(simConfig.getNumMachines(index));
		experiment.setUtilLevel(simConfig.getUtilLevel(index));
		experiment.setDueDateFactor(simConfig.getDueDateFactor(index));
		experiment.setWeights(simConfig.getWeight(index));
		experiment.setOpProcTime(simConfig.getMinOpProc(index), simConfig.getMaxOpProc(index));
		experiment.setNumOps(simConfig.getMinNumOps(index), simConfig.getMaxNumOps(index));

		BasicJobStatCollector statCollector = new BasicJobStatCollector();
		statCollector.setIgnoreFirst(simConfig.getNumIgnore());

		experiment.setShopListener(new NotifierListener[]{statCollector});
		if (workstationListener != null) { experiment.addMachineListener(workstationListener); }
		experiment.setStopAfterNumJobs(simConfig.getStopAfterNumJobs());
		experiment.setSequencingRule(rule);
		experiment.setScenario(DynamicShopExperiment.Scenario.JOB_SHOP);

		return experiment;
	}

	public Object clone() {
		JasimaGPProblem newObject = (JasimaGPProblem) super.clone();

		newObject.shouldSetContext = shouldSetContext;
		newObject.input = (JasimaGPData) input.clone();

		newObject.simConfig = simConfig;
		newObject.simSeed = simSeed;
		newObject.rand = (MersenneTwisterFast) rand.clone();

		newObject.tracker = tracker;

		newObject.workstationListener = workstationListener;
		((JasimaGPData) newObject.input).setWorkStationListener(newObject.workstationListener);

		return newObject;
	}

}
