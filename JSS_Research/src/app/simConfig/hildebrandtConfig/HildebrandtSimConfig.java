package app.simConfig.hildebrandtConfig;

import jasima.core.random.continuous.DblStream;
import app.simConfig.DynamicSimConfig;

public class HildebrandtSimConfig extends DynamicSimConfig {

	@Override
	public int getNumMachines(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DblStream getProcTime(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getUtilLevel(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DblStream getDueDateFactor(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DblStream getWeight(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMinNumOps(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxNumOps(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumIgnore() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getStopAfterNumJobs() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumConfigs() {
		// TODO Auto-generated method stub
		return 0;
	}

}
