package jss.evolution.sample;

import jss.IJob;
import jss.IMachine;
import ec.gp.GPData;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class BasicData extends GPData {

	private static final long serialVersionUID = 5L;

	private IMachine machine = null;
	private IJob job = null;

	private double priority;

	/**
	 * TODO javadoc.
	 * @param machine
	 */
	public void setMachine(IMachine machine) {
		this.machine = machine;
	}

	/**
	 * TODO javadoc.
	 * @param job
	 */
	public void setJob(IJob job) {
		this.job = job;
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	public double getReleaseTime() {
		return job.getReleaseTime();
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	public double getProcessingTime() {
		return job.getProcessingTime(machine);
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	public double getSetupTime() {
		return job.getSetupTime(machine);
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	public double getDueDate() {
		return job.getDueDate(machine);
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	public double getPriority() {
		return priority;
	}

	/**
	 * TODO javadoc.
	 * @param priority
	 */
	public void setPriority(double priority) {
		this.priority = priority;
	}

	public void clear() {
		machine = null;
		job = null;
	}
}
