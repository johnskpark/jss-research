package jss;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import jss.problem.IJob;
import jss.problem.IMachine;

/**
 * Represents a basic job in a job shop scheduling problem instance. Has the same
 * processing time, setup time and due date for all machines in the shop, and has
 * to visit all machines to be considered completed.
 *
 * @author parkjohn
 *
 */
public class BasicJob implements IJob {

	private double releaseTime = 0;
	private double processingTime = 0;

	private double setupTime = 0;
	private double dueDate = 0;

	private Set<IMachine> machinesVisited = new HashSet<IMachine>();

	/**
	 * Initialise the job.
	 * @param releaseTime earliest time that the job can be inserted into a machine.
	 * @param processingTime time taken for the job to be processed after it is setup on the machine.
	 * @param setupTime time taken to setup the job on the machine.
	 * @param dueDate the time that the job is expected to be completed by.
	 */
	public BasicJob(double releaseTime, double processingTime, double setupTime, double dueDate) {
		this.releaseTime = releaseTime;
		this.processingTime = processingTime;
		this.setupTime = setupTime;
		this.dueDate = dueDate;
	}

	@Override
	public double getReleaseTime() {
		return releaseTime;
	}

	@Override
	public double getProcessingTime(IMachine machine) {
		return processingTime;
	}

	@Override
	public double getSetupTime(IMachine machine) {
		return setupTime;
	}

	@Override
	public double getDueDate(IMachine machine) {
		return dueDate;
	}

	@Override
	public void visitMachine(IMachine machine) {
		machinesVisited.add(machine);
	}

	@Override
	public boolean isProcessable(IMachine machine) {
		return !machinesVisited.contains(machine);
	}
}
