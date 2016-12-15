package app.simConfig.huntConfig;

import jasima.core.random.continuous.DblStream;
import app.simConfig.DynamicSimConfig;

// TODO This is broken.
public class TrainSimConfig extends DynamicSimConfig {

	private static final int NUM_IGNORE = 500;
	private static final int STOP_AFTER_NUM_JOBS = 2500;

	private DynamicSimConfig fourOp = new FourOpSimConfig();
	private DynamicSimConfig eightOp = new EightOpSimConfig();

	@Override
	public void setSeed(long s) {
		super.setSeed(s);
		fourOp.setSeed(s);
		eightOp.setSeed(s);
	}

	@Override
	public int getNumMachines(int index) {
		if (index < fourOp.getNumConfigs()) {
			return fourOp.getNumMachines(index);
		} else {
			return eightOp.getNumMachines(index-fourOp.getNumConfigs());
		}
	}

	@Override
	public DblStream getProcTime(int index) {
		if (index < fourOp.getNumConfigs()) {
			return fourOp.getProcTime(index);
		} else {
			return eightOp.getProcTime(index-fourOp.getNumConfigs());
		}
	}

	@Override
	public double getUtilLevel(int index) {
		if (index < fourOp.getNumConfigs()) {
			return fourOp.getUtilLevel(index);
		} else {
			return eightOp.getUtilLevel(index-fourOp.getNumConfigs());
		}
	}

	@Override
	public DblStream getDueDateFactor(int index) {
		if (index < fourOp.getNumConfigs()) {
			return fourOp.getDueDateFactor(index);
		} else {
			return eightOp.getDueDateFactor(index-fourOp.getNumConfigs());
		}
	}

	@Override
	public DblStream getWeight(int index) {
		if (index < fourOp.getNumConfigs()) {
			return fourOp.getWeight(index);
		} else {
			return eightOp.getWeight(index-fourOp.getNumConfigs());
		}
	}

	@Override
	public int getMinNumOps(int index) {
		if (index < fourOp.getNumConfigs()) {
			return fourOp.getMinNumOps(index);
		} else {
			return eightOp.getMinNumOps(index-fourOp.getNumConfigs());
		}
	}

	@Override
	public int getMaxNumOps(int index) {
		if (index < fourOp.getNumConfigs()) {
			return fourOp.getMaxNumOps(index);
		} else {
			return eightOp.getMaxNumOps(index-fourOp.getNumConfigs());
		}
	}

	@Override
	public int getNumIgnore() {
		return NUM_IGNORE;
	}

	@Override
	public int getStopArrivalsAfterNumJobs() {
		return STOP_AFTER_NUM_JOBS;
	}

	@Override
	public int getNumConfigs() {
		return fourOp.getNumConfigs() + eightOp.getNumConfigs();
	}

}
