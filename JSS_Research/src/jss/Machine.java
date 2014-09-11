package jss;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a machine in a job shop scheduling problem instance.
 *
 * @author parkjohn
 *
 */
public class Machine {

	List<Job> processedJobs = new ArrayList<Job>();
	Job currentJob = null;

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

	/**
	 * Get the current job being processed.
	 * @return the current job being processed, or null if no jobs are being processed
	 */
	Job getCurrentJob() {
		return currentJob;
	}

	/**
	 * Starts processing the job, as long as the machine does not have a job currently
	 * being processed on it. Otherwise, the job is immediately rejected and
	 * a RuntimeException is thrown.
	 * @param job the job to start processing
	 * @throws RuntimeException if the machine is currently busy
	 */
	void processJob(Job job) throws RuntimeException {
		// TODO: placeholder
	}

	/**
	 * Callback method to be called from the timer to update the current job status.
	 */
	void updateStatus() {
		// TODO: placeholder
	}

	/**
	 * Get whether the machine is available to process a job.
	 * @return true if there is no job being processed, false otherwise
	 */
	boolean isAvailable() {
		return currentJob != null;
	}

	/**
	 * Return when the machine is next available to process a job.
	 * @return TODO write some bullshit javadoc here
	 */
	double getTimeAvailable() {
		return timeAvailable;
	}
}
