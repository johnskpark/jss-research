package jss;


/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public interface Subscriber {

	/**
	 * TODO javadoc.
	 *
	 * Callback.
	 */
	public void onMachineFeed(IMachine machine);

	/**
	 * TODO javadoc.
	 */
	public void onJobFeed(IJob job);
}
