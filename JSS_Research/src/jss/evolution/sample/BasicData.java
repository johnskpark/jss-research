package jss.evolution.sample;

import jss.IJob;
import jss.IMachine;
import ec.gp.GPData;

public class BasicData extends GPData {

	private static final long serialVersionUID = 5L;

	private IMachine machine = null;
	private IJob job = null;

	private double priority;

	public void setMachine(IMachine machine) {
		this.machine = machine;
	}

	public void setJob(IJob job) {
		this.job = job;
	}

	public double getReleaseTime() {
		return job.getReleaseTime();
	}

	public double getProcessingTime() {
		return job.getProcessingTime(machine);
	}

	public double getSetupTime() {
		return job.getSetupTime(machine);
	}

	public double getDueDate() {
		return job.getDueDate(machine);
	}

	public double getPriority() {
		return priority;
	}

	public void setPriority(double priority) {
		this.priority = priority;
	}

	public void clear() {
		machine = null;
		job = null;
	}
}
