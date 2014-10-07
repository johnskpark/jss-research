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
	 * @param machine
	 * @param time
	 */
	public void onMachineFeed(IMachine machine, double time);

	/**
	 * TODO javadoc.
	 * @param job
	 * @param time
	 */
	public void onJobFeed(IJob job, double time);
}
