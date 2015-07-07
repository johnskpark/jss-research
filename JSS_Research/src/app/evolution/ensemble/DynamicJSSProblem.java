package app.evolution.ensemble;

import java.util.Random;

import app.evolution.IJasimaFitness;
import app.evolution.IJasimaGPProblem;
import app.evolution.coop.IJasimaCoopFitness;
import app.simConfig.AbsSimConfig;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPProblem;
import ec.util.Parameter;

public class DynamicJSSProblem extends GPProblem implements IJasimaGPProblem {

	public static final String P_FITNESS = "fitness";
	public static final String P_SIMULATOR = "simulator";
	public static final String P_SEED = "seed";

	public static final long DEFAULT_SEED = 15;

	private AbsSimConfig simConfig;
	private long simSeed;
	private Random rand;

	private IJasimaFitness fitness;

	private int numSubpops;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		setupSimulator(state, base);
		setupFitness(state, base);
	}

	private void setupSimulator(final EvolutionState state, final Parameter base) {
		simConfig = (AbsSimConfig) state.parameters.getInstanceForParameterEq(base.push(P_SIMULATOR), null, AbsSimConfig.class);

		Parameter simBase = base.push(P_SIMULATOR);
		simSeed = state.parameters.getLongWithDefault(simBase.push(P_SEED), null, DEFAULT_SEED);
		rand = new Random(simSeed);
	}

	private void setupFitness(final EvolutionState state, final Parameter base) {
		fitness = (IJasimaCoopFitness) state.parameters.getInstanceForParameterEq(base.push(P_FITNESS), null, IJasimaCoopFitness.class);
	}

	@Override
	public void evaluate(EvolutionState state, Individual ind,
			int subpopulation, int threadnum) {
		// TODO Auto-generated method stub

	}

	@Override
	public AbsSimConfig getSimConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumInds() {
		// TODO Auto-generated method stub
		return 0;
	}

}
