package app.evolution.simConfig.rachelConfig;

import app.SimulatorConfiguration;

public class TestSimConfig extends SimulatorConfiguration {

	@Override
	public int getNumMachines() {
		return 10;
	}
	
	@Override
	public double getUtilLevel() {
		return 0.85;
	}
	
	@Override
	public int getMinOpProc() {
		return 1;
	}

	@Override
	public int getMaxOpProc() {
		return 49;
	}

	@Override
	public int getMinNumOps() {
		return 4;
	}

	@Override
	public int getMaxNumOps() {
		return 4;
	}

}
