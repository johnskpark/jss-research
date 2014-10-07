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
	 * @return the current job being processed, or null if no jobs are being processed.
	 */
	public IJob getCurrentJob();

	/**
	 * Get the last job that was processed.
	 * @return the last job that was processed, or null if no jobs are being processed.
	 */
	public IJob getLastProcessedJob();

	/**
	 * Get the list of all the jobs processed by the machine.
	 * @return a list of the jobs processed by the machine
	 */
	public List<IJob> getProcessedJobs();

	/**
	 * Starts processing the job, as long as the following criterion are satisfied:
	 * - The machine does not have a job currently being processed on it.
	 * - The job has not already been processed on the particular machine.
	 * - The job cannot be processed on the machine.
	 *  Otherwise, the job is immediately rejected and a RuntimeException is thrown.
	 * @param job the job to start processing
	 * @param time TODO javadoc.
	 * @throws RuntimeException if the job cannot be processed on the machine
	 */
	public void processJob(IJob job, double time) throws RuntimeException;

	/**
	 * Get whether the machine is available to process a job.
	 * @return if the machine is available, false otherwise.
	 */
	public boolean isAvailable();

	/**
	 * Return when the machine is next available to process a job.
	 * @return the time when the machine will next be available.
	 */
	public double getTimeAvailable();

	/**
	 * TODO javadoc.
	 * @param time
	 */
	public void updateStatus(double time);

	/**
	 * Reset any processing done on this machine.
	 */
	public void reset();
}
