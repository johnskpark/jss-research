package jss;


/**
 * Interface for classes that subscribes to the @see ISubscriptionHandler for
 * updates on the Job Shop Scheduling environment. ISubscriber gets subscribed
 * to the ISubscriptionHandler by calling the method
 * @see onSubscriptionRequest.
 *
 * After that point on, the ISubscriptionHandler will use the callback methods
 * defined by the ISubscriber to send it updates.
 *
 * ISubscriber should be used in conjunction with @see ISolver to solve the
 * job shop scheduling problem instances.
 *
 * @author parkjohn
 *
 */
public interface ISubscriber {

	/**
	 * Callback method that the ISubscriptionHandler uses to notify the
	 * subscriber that an update occurred on the machine.
	 * @param machine The machine for which the event occurred for.
	 * @param time The time when the machine event occurred.
	 */
	public void onMachineFeed(IMachine machine, double time);

	/**
	 * Callback method that the ISubscriptionHandler uses to notify the
	 * subscriber that an update occurred on the job.
	 * @param job The job for which the event occurred for.
	 * @param time The time when the job event occurred.
	 */
	public void onJobFeed(IJob job, double time);
}
