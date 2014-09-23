package jss;


public interface SubscriptionHandler {

	/**
	 * TODO javadoc.
	 * @param s
	 */
	public void onSubscriptionRequest(Subscriber s);

	/**
	 * TODO javadoc.
	 */
	public void sendMachineFeed(IMachine machine);

	/**
	 * TODO javadoc.
	 */
	public void sendJobFeed(IJob job);
}
