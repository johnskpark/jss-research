package jss;

/**
 * Represents a job in a job shop scheduling problem instance.
 *
 * @author parkjohn
 *
 */
public class Job {

	private double releaseTime;
	private double processingTime;

	private double setupTime;
	private double dueDate;

	// TODO: other job features here, such as which machines it was processed on.

	public Job(double releaseTime, double processingTime, double setupTime, double dueDate) {
		this.releaseTime = releaseTime;
		this.processingTime = processingTime;
		this.setupTime = setupTime;
		this.dueDate = dueDate;
	}

	/**
	 * Get the release time of the job.
	 * @return
	 */
	public double getReleaseTime() {
		return releaseTime;
	}

	/**
	 * Get the processing time of the job.
	 * @return
	 */
	public double getProcessingTime() {
		return processingTime;
	}

	/**
	 * Get the setup time of the job.
	 * @return
	 */
	public double getSetupTime() {
		return setupTime;
	}

	/**
	 * Get the due date of the job.
	 * @return
	 */
	public double getDueDate() {
		return dueDate;
	}
}
