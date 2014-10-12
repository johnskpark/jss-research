package jss;


/**
 * Interface for classes that subscribes to the @see ISubscriptionHandler
 *
 *
 * @author parkjohn
 *
 */
public interface ISubscriber {

	/**
	 * Callback method that the broadcaster. TODO
	 * @param machine The machine for which the event occurred for.
	 * @param time The time when the machine event occurred.
	 */
	public void onMachineFeed(IMachine machine, double time);

	/**
	 * TODO javadoc.
	 * @param job The job for which the event occurred for.
	 * @param time The time when the job event occurred.
	 */
	public void onJobFeed(IJob job, double time);
}
