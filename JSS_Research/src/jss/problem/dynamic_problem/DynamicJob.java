package jss.problem.dynamic_problem;

import jss.IEvent;
import jss.ISubscriptionHandler;
import jss.problem.BaseJob;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class DynamicJob extends BaseJob {

	private IEvent jobReadyEvent;
	private ISubscriptionHandler subscriptionHandler;

	/**
	 * Generate a new instance of a static job for the static Job Shop
	 * Scheduling problem instance.
	 */
	public DynamicJob(ISubscriptionHandler handler) {
		super();
		
		subscriptionHandler = handler;
		jobReadyEvent = new JobReadyEvent(this);
	}

	/// IEventHandler

	@Override
	public boolean hasEvent() {
		return jobReadyEvent != null;
	}

	@Override
	public IEvent getNextEvent() {
		return jobReadyEvent;
	}

	@Override
	public double getNextEventTime() {
		return getReadyTime();
	}

	// An event class that represents a job being released into the market.
	private class JobReadyEvent implements IEvent {
		private DynamicJob job;

		public JobReadyEvent(DynamicJob job) {
			this.job = job;
		}

		@Override
		public void trigger() {
			job.updateStatus();
		}
	}

	/**
	 * Update the status of the dynamic job.
	 */
	public void updateStatus() {
		jobReadyEvent = null;
		subscriptionHandler.sendJobFeed(this, getReadyTime());
	}

}
