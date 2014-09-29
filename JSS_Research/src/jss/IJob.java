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
	 * Visit the machine to be processed. The logic for whether it is
	 * valid for the job to be processed by the machine will be handled
	 * in the machine logic.
	 * @param machine the machine to visit
	 * @throws RuntimeException if the machine is not next in line to be visited
	 */
	public void visitMachine(IMachine machine) throws RuntimeException;

	/**
	 * Get the next machine that needs to be visited by the job.
	 * @return The next machine to visit by the job to process it.
	 */
	public IMachine getNextMachine();

	/**
	 * Get whether the job can be processed on the machine or not. TODO
	 * @return
	 */
	public boolean isProcessable(IMachine machine);

	/**
	 * TODO javadoc.
	 * @return
	 */
	public boolean isCompleted();

	/**
	 * Reset any processing done for this job.
	 */
	public void reset();
}
