package jss.problem.static_problem;

import jss.IJob;
import jss.problem.BaseInstance;

/**
 * Really, really basic problem instance. TODO write more, especially what static means.
 *
 * @author parkjohn
 *
 */
public class StaticInstance extends BaseInstance {

	private double upperBound;
	private double lowerBound;

	/**
	 * Generate a new static job shop scheduling problem instance.
	 */
	public StaticInstance() {
	}

//	public void addJob(StaticJob job) {
//		getJobs().add(job);
//		getIncompleteJobs().add(job);
//	}

	@Override
	public int getWarmUp() {
		return 0;
	}

	@Override
	public boolean isWarmUpComplete() {
		return true;
	}

	@Override
	public void initialise() {
		for (IJob job : getJobs()) {
			job.getCurrentMachine().addWaitingJob(job);
		}
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	public double getUpperBound() {
		return upperBound;
	}

	/**
	 * TODO javadoc.
	 * @param upperBound
	 */
	public void setUpperBound(double upperBound) {
		this.upperBound = upperBound;
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	public double getLowerBound() {
		return lowerBound;
	}

	/**
	 * TODO javadoc.
	 * @param lowerBound
	 */
	public void setLowerBound(double lowerBound) {
		this.lowerBound = lowerBound;
	}

}
