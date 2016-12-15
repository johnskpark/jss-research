package app.simConfig;

import java.util.Random;

import jasima.core.random.continuous.DblStream;

public abstract class DynamicBreakdownSimConfig implements SimConfig {

	private long jobSeed;
	private long machineSeed;

	private Random jobRand;
	private Random machineRand;

	public long getJobSeed() {
		return jobSeed;
	}

	public long getMachineSeed() {
		return machineSeed;
	}

	public void setJobSeed(long s) {
		jobSeed = s;
		jobRand = new Random(jobSeed);
	}

	public void setMachineSeed(long s) {
		machineSeed = s;
		machineRand = new Random(machineSeed);
	}

	public long getLongValueForJob() {
		return jobRand.nextLong();
	}

	public long getLongValueForMachine() {
		return machineRand.nextLong();
	}

	public void reset() {
		setJobSeed(jobSeed);
		setMachineSeed(machineSeed);
	}

	public abstract DblStream getProcTime(int index);

	public abstract double getUtilLevel(int index);

	public abstract DblStream getDueDateFactor(int index);

	public abstract DblStream getWeight(int index);

	public abstract int getMinNumOps(int index);

	public abstract int getMaxNumOps(int index);

	public abstract int getNumIgnore();

	public abstract int getStopArrivalsAfterNumJobs();

	public abstract double getRepairTimeFactor(int index);

	public abstract double getBreakdownLevel(int index);

}
