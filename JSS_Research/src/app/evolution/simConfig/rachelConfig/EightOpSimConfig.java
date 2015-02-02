package app.evolution.simConfig.rachelConfig;

import jasima.core.random.continuous.DblStream;
import app.SimulatorConfiguration;

public class EightOpSimConfig extends SimulatorConfiguration {

	private static final int MIN_OP_PROC = 1;
	private static final int MAX_OP_PROC = 49;
	private static final double[] UTIL_LEVEL = new double[]{0.85, 0.95};
	private static final int NUM_OPS = 8;

	private static final int NUM_MACHINES = 10;
	
	private static final int NUM_CONFIG = 2;

	private DblStream dueDateFactorStream;
	private DblStream weightStream;
	
	@Override
	public void setSeed(long seed) {
		super.setSeed(seed);
		
		dueDateFactorStream = new TrainDDFStream(seed);
		weightStream = new WeightStream(seed);
	}
	
	@Override
	public int getNumMachines(int index) {
		return NUM_MACHINES;
	}
	
	@Override
	public int getMinOpProc(int index) {
		return MIN_OP_PROC;
	}

	@Override
	public int getMaxOpProc(int index) {
		return MAX_OP_PROC;
	}

	@Override
	public double getUtilLevel(int index) {
		return UTIL_LEVEL[index];
	}
	
	@Override
	public DblStream getDueDateFactor(int index) {
		return dueDateFactorStream; 
	}

	@Override
	public DblStream getWeight(int index) {
		return weightStream;
	}
	
	@Override
	public int getMinNumOps(int index) {
		return NUM_OPS;
	}

	@Override
	public int getMaxNumOps(int index) {
		return NUM_OPS;
	}

	@Override
	public int getNumConfigs() {
		return NUM_CONFIG;
	}
	
}
