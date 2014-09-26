package jss;


/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public interface ISubscriber {

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
