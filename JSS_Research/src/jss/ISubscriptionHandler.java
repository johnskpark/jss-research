package jss;


public interface ISubscriptionHandler {

	/**
	 * TODO javadoc.
	 * @param subscriber
	 */
	public void onSubscriptionRequest(ISubscriber subscriber);

	/**
	 * TODO javadoc.
	 * @param machine
	 * @param time
	 */
	public void sendMachineFeed(IMachine machine, double time);

	/**
	 * TODO javadoc.
	 * @param machine
	 * @param time
	 */
	public void sendJobFeed(IJob job, double time);
}
