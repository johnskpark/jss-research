package jss;


/**
 * Generic interface for job definition in a Job Shop Scheduling problem.
 *
 * @author parkjohn
 *
 */
public interface IJob {

	/**
	 * Get the release time of the job.
	 * @return The time that the job is first released into the problem.
	 */
	public double getReleaseTime();

	/**
	 * Get the processing time of the job for the particular machine.
	 * @param machine The machine that will be processing the job.
	 * @return The time it takes to process the job on the machine. If the job
	 *         cannot be processed at the particular machine, then
	 *         Double.POSITIVE_INFINITY will be returned.
	 */
	public double getProcessingTime(IMachine machine);

	/**
	 * Get the setup time of the job for the particular machine.
	 * @param machine The machine that will be processing the job.
	 * @return The time it takes to setup the job on the machine. If the job
	 *         cannot be processed at the particular machine, then
	 *         Double.POSITIVE_INFINITY will be returned.
	 */
	public double getSetupTime(IMachine machine);

	/**
	 * Get the due date of the job for the particular machine.
	 * @param machine The machine that will be processing the job.
	 * @return The due date of the job on the machine. If the job cannot be
	 *         processed at the particular machine, then
	 *         Double.POSITIVE_INFINITY will be returned.
	 */
	public double getDueDate(IMachine machine);

	/**
	 * Get the penalty factor for tardy jobs.
	 * @param machine
	 * @return
	 */
	public double getPenalty(IMachine machine);

	/**
	 * Callback function that declares that the job has been last processed on
	 * the specified machine.
	 * @param machine the machine for which the job has been last processed on.
	 * @throws RuntimeException if the machine is not next in line to be visited
	 */
	public void processedOnMachine(IMachine machine) throws RuntimeException;

	/**
	 * Get the next machine that needs to be visited by the job.
	 * @return The next machine to visit by the job to process it.
	 */
	public IMachine getNextMachine();

	/**
	 * Get the last machine that the job was processed on.
	 * @return The last machine that was visited by the job.
	 */
	public IMachine getLastMachine();

	/**
	 * Get whether the job has an operation on the particular machine or not.
	 * Jobs should not be processed on machines that it does not have an
	 * operation on.
	 * @return True if the job has an operation on the specified machine,
	 *         false otherwise.
	 */
	public boolean isProcessable(IMachine machine);

	/**
	 * Get whether all operations have been completed for the job.
	 * @return True if the job does not need to be processed any further,
	 *         false otherwise.
	 */
	public boolean isCompleted();

	/**
	 * Reset any processing done for this job.
	 */
	public void reset();
}
