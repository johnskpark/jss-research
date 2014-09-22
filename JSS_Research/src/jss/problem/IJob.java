package jss.problem;

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
	 */
	public void visitMachine(IMachine machine); // TODO: probably come up with a different name later.

	/**
	 * Get whether the job can be processed on the machine or not.
	 * @return
	 */
	public boolean isProcessable(IMachine machine); // TODO: probably come up with a different name later.
}
