package jss;


public interface ISubscriptionHandler {

	/**
	 * TODO javadoc.
	 * @param s
	 */
	public void onSubscriptionRequest(ISubscriber subscriber);

	/**
	 * TODO javadoc.
	 */
	public void sendMachineFeed(IMachine machine);

	/**
	 * TODO javadoc.
	 */
	public void sendJobFeed(IJob job);
}
