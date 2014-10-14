package jss;

import java.util.List;

/**
 * Generic interface for machine definition.
 *
 * @author parkjohn
 *
 */
public interface IMachine {

	/**
	 * Get the current job being processed.
	 * @return The current job being processed, or null if no jobs are being processed.
	 */
	public IJob getCurrentJob();

	/**
	 * Get the last job that was processed.
	 * @return The last job that was processed, or null if no jobs are being processed.
	 */
	public IJob getLastProcessedJob();

	/**
	 * Get the list of all the jobs processed by the machine.
	 * @return A list of the jobs processed by the machine
	 */
	public List<IJob> getProcessedJobs();

	/**
	 * Starts processing the job, as long as the following criterion are satisfied:
	 * - The machine does not have a job currently being processed on it.
	 * - The job has not already been processed on the particular machine.
	 * - The job cannot be processed on the machine.
	 *  Otherwise, the job is immediately rejected and a RuntimeException is thrown.
	 * @param job The job to start processing.
	 * @param time The time to start processing the job.
	 * @throws RuntimeException If the job cannot be processed on the machine
	 */
	public void processJob(IJob job, double time) throws RuntimeException;

	/**
	 * Get whether the machine is available to process a job.
	 * @return If the machine is available, false otherwise.
	 */
	public boolean isAvailable();

	/**
	 * Return when the machine is next available to process a job.
	 * @return The time when the machine will next be available.
	 */
	public double getTimeAvailable();

	/**
	 * Update the status of the machine for the specified time. This includes
	 * any processing being completed, machine breaking down, etc.
	 * @param time The time to update the machine to.
	 */
	public void updateStatus(double time);

	/**
	 * Reset any processing done on this machine.
	 */
	public void reset();
}
