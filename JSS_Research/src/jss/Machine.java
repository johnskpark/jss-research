package jss;

import java.util.ArrayList;
import java.util.List;

import jss.problem.IJob;
import jss.problem.IMachine;

/**
 * Represents a basic machine in a job shop scheduling problem instance.
 *
 * @author parkjohn
 *
 */
public class Machine implements IMachine {
	List<IJob> processedJobs = new ArrayList<IJob>();
	IJob currentJob = null;

	double timeAvailable = 0;
	MachineTimer timer = null;

	/**
	 * Default constructor for initialising the Machine.
	 * @param timer universal timer that is used by all of the machines.
	 */
	public Machine(MachineTimer timer) {
		this.timer = timer;
		this.timer.addMachine(this);
	}

	@Override
	public IJob getCurrentJob() {
		return currentJob;
	}

	@Override
	public List<IJob> getProcessedJobs() {
		return processedJobs;
	}

	@Override
	public void processJob(IJob job) throws RuntimeException {
		if (currentJob != null) {
			throw new RuntimeException("Attempted to process a job while the machine was still running");
		}

		currentJob = job;
		timeAvailable =
				Math.max(timer.getCurrentTime(), job.getReleaseTime()) +
				job.getSetupTime(this) +
				job.getProcessingTime(this);
	}

	@Override
	public void updateStatus() {
		double currentTime = timer.getCurrentTime();

		if (currentTime >= timeAvailable) {
			processedJobs.add(currentJob);
			currentJob = null;
		}
	}

	@Override
	public boolean isAvailable() {
		return currentJob == null;
	}

	@Override
	public double getTimeAvailable() {
		return timeAvailable;
	}
}
