package jss;


/**
 * Generic interface for job definition.
 *
 * TODO this will need to be changed. At the moment, it doesn't
 * take into account various scenarios:
 *
 * - Job's property changes
 * - Job needs to be processed on certain order
 * - Machines may break down, requiring jobs to be rerouted
 *
 * Because of this, I might need to rewrite the interface again.
 *
 * @author parkjohn
 *
 */
public interface IJob {

	/**
	 * Get the release time of the job.
	 * @return
	 */
	public double getReleaseTime();

	/**
	 * Get the processing time of the job.
	 * @param machine TODO
	 * @return
	 */
	public double getProcessingTime(IMachine machine);

	/**
	 * Get the setup time of the job.
	 * @param machine TODO
	 * @return
	 */
	public double getSetupTime(IMachine machine);

	/**
	 * Get the due date of the job.
	 * @param machine TODO
	 * @return
	 */
	public double getDueDate(IMachine machine);

	/**
	 * Visit the machine to be processed. The logic for whether it is
	 * valid for the job to be processed by the machine will be handled
	 * in the machine logic.
	 * @param machine the machine to visit
	 * @throws RuntimeException if the machine is not next in line to be visited
	 */
	public void visitMachine(IMachine machine) throws RuntimeException;

	/**
	 * TODO javadoc.
	 * @return
	 */
	public IMachine getNextMachine();

	/**
	 * Get whether the job can be processed on the machine or not.
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
