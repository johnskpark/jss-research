package app.evolution.pickardt;

import jasima.core.experiment.Experiment;
import jasima.core.util.observer.NotifierListener;
import jasima.shopSim.core.PR;
import jasima.shopSim.models.dynamicShop.DynamicShopExperiment;
import jasima.shopSim.prioRules.basic.CR;
import jasima.shopSim.prioRules.basic.EDD;
import jasima.shopSim.prioRules.basic.MDD;
import jasima.shopSim.prioRules.basic.MOD;
import jasima.shopSim.prioRules.basic.ODD;
import jasima.shopSim.prioRules.basic.SLK;
import jasima.shopSim.prioRules.basic.SPT;
import jasima.shopSim.prioRules.basic.SRPT;
import jasima.shopSim.prioRules.batch.BATCS;
import jasima.shopSim.prioRules.setup.ATCS;
import jasima.shopSim.prioRules.weighted.WMDD;
import jasima.shopSim.prioRules.weighted.WMOD;
import jasima.shopSim.prioRules.weighted.WSPT;
import jasima.shopSim.util.BasicJobStatCollector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import app.evolution.IWorkStationListenerEvolveFactory;
import app.evolution.pickardt.presetRules.PRCR;
import app.evolution.pickardt.presetRules.PRFCFS;
import app.listener.IWorkStationListener;
import app.node.CompositePR;
import app.node.NodeData;
import app.simConfig.AbsSimConfig;
import app.util.RuleParser;
import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleProblemForm;
import ec.util.ParamClassLoadException;
import ec.util.Parameter;
import ec.vector.IntegerVectorIndividual;

public class JasimaPickardtProblem extends Problem implements SimpleProblemForm {

	private static final long serialVersionUID = 4598953667123971268L;

	public static final String P_RULE = "ruleFile";
	public static final String P_FITNESS = "fitness";

	public static final String P_SIMULATOR = "simulator";
	public static final String P_SEED = "seed";

	public static final String P_WORKSTATION = "workstation";

	public static final long DEFAULT_SEED = 15;

	private RuleParser parser = new RuleParser();

	private PR gpRule = null;
	private PR[] rules = new PR[]{
			// PRFCFS, PRCR, WSPT, WMDD, WMOD, CR, SPT, LWKR, EDD, MDD, ODD, MOD, SLACK,
			// ATCS, BATCS (k1=2.0,3.0,5.0,6.0, k2=0.005,0.01,0.1,1.0)
			null, // placeholder for GP rule.
			new PRFCFS(),
			new PRCR(),
			new WSPT(),
			new WMDD(),
			new WMOD(),
			new CR(),
			new SPT(),
			new SRPT(),
			new EDD(),
			new MDD(),
			new ODD(),
			new MOD(),
			new SLK(),
			new ATCS(2.0, 0.005),
			new ATCS(3.0, 0.01),
			new ATCS(5.0, 0.1),
			new ATCS(6.0, 1.0),
			new BATCS(2.0, 0.005),
			new BATCS(3.0, 0.01),
			new BATCS(5.0, 0.1),
			new BATCS(6.0, 1.0)
	};

	private static final int NUM_RULES = 22;

	private IJasimaPickardtFitness fitness;

	private AbsSimConfig simConfig;
	private long simSeed;

	private IWorkStationListener workstationListener;
	private NodeData nodeData = new NodeData();

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

        // Setup the workstation listener.
        try {
        	IWorkStationListenerEvolveFactory factory = (IWorkStationListenerEvolveFactory) state.parameters.getInstanceForParameterEq(base.push(P_WORKSTATION), null, IWorkStationListener.class);
        	factory.setup(state, base.push(P_WORKSTATION));

        	workstationListener = factory.generateWorkStationListener();

    		// Feed in the shop simulation listener to input.
            nodeData.setWorkStationListener(workstationListener);
        } catch (ParamClassLoadException ex) {
        	state.output.warning("No workstation listener provided for JasimaMultilevelProblem.");
        }

		try {
			File file = new File(state.parameters.getString(base.push(P_RULE), null));
			FileInputStream fileInputStream = new FileInputStream(file);

			byte[] buffer = new byte[(int)file.length()];
			fileInputStream.read(buffer);
			fileInputStream.close();

			gpRule = new CompositePR(parser.getRuleFromString(new String(buffer, "UTF-8")), nodeData);
			rules[0] = gpRule;
		} catch (IOException ex) {
			state.output.fatal("Error while reading GP rule file " + ex.getMessage());
		}

		// Setup the simulator configurations.
		simConfig = (AbsSimConfig) state.parameters.getInstanceForParameterEq(base.push(P_SIMULATOR), null, AbsSimConfig.class);
		setupSimulator(state, base.push(P_SIMULATOR));

		// Setup the fitness.
		fitness = (IJasimaPickardtFitness) state.parameters.getInstanceForParameterEq(base.push(P_FITNESS), null, IJasimaPickardtFitness.class);
		setupFitness(state, base.push(P_FITNESS));
	}

	private void setupSimulator(final EvolutionState state, final Parameter simBase) {
		simSeed = state.parameters.getLongWithDefault(simBase.push(P_SEED), null, DEFAULT_SEED);
	}

	@Override
	public void prepareToEvaluate(final EvolutionState state, final int threadnum) {
		// Reset the seed for the simulator.
		simConfig.setSeed(simSeed);
	}

	private void setupFitness(final EvolutionState state, final Parameter fitnessBase) {
		// fitness.setProblem(this);
	}

	@Override
	public void evaluate(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		if (!ind.evaluated) {
			checkConstraints(state, ind);

			IntegerVectorIndividual vectorInd = (IntegerVectorIndividual) ind;

			PR[] designatedRules = new PR[vectorInd.genomeLength()];
			for (int i = 0; i < vectorInd.genomeLength(); i++) {
				designatedRules[i] = rules[vectorInd.genome[i]];
			}

			for (int i = 0; i < simConfig.getNumConfigs(); i++) {
				Experiment experiment = getExperiment(state, designatedRules, i);

				experiment.runExperiment();

				fitness.accumulateFitness(i, experiment.getResults());
			}

			fitness.setFitness(state, ind);
			fitness.clear();

			ind.evaluated = true;
		}
	}

	// Mainly for debugging purposes.
	private void checkConstraints(final EvolutionState state, final Individual ind) {
		if (!(ind instanceof IntegerVectorIndividual)) {
			state.output.fatal("Individual is not an instance of IntegerVectorIndividual");
		}

		IntegerVectorIndividual vectorInd = (IntegerVectorIndividual) ind;
		for (int i = 0; i < simConfig.getNumConfigs(); i++) {
			if (vectorInd.genomeLength() != simConfig.getNumMachines(i)) {
				state.output.fatal("The genome length does not match the number of machines. Genome length: " + vectorInd.genomeLength() + ". Number of machines: " + simConfig.getNumMachines(i));
			}

			for (int j = 0; j < vectorInd.genomeLength(); j++) {
				if (vectorInd.genome[j] < 0 || vectorInd.genome[j] >= NUM_RULES) {
					state.output.fatal("Invalid genome value at index " + j + ". Value: " +vectorInd.genome[j]);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Experiment getExperiment(final EvolutionState state, PR[] rules, int index) {
		DynamicShopExperiment experiment = new DynamicShopExperiment();

		experiment.setInitialSeed(simConfig.getLongValue());
		experiment.setNumMachines(simConfig.getNumMachines(index));
		experiment.setUtilLevel(simConfig.getUtilLevel(index));
		experiment.setDueDateFactor(simConfig.getDueDateFactor(index));
		experiment.setWeights(simConfig.getWeight(index));
		experiment.setOpProcTime(simConfig.getMinOpProc(index), simConfig.getMaxOpProc(index));
		experiment.setNumOps(simConfig.getMinNumOps(index), simConfig.getMaxNumOps(index));

		experiment.setShopListener(new NotifierListener[]{new BasicJobStatCollector()});
		experiment.setSequencingRules(rules);
		experiment.setScenario(DynamicShopExperiment.Scenario.JOB_SHOP);

		return experiment;
	}

}
