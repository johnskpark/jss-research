package jss;

/**
 * Interface for classes that are subscribed to for updates. These handlers send
 * out feeds to subscribers whenever the @see Simulator triggers events
 * pertaining to machines and jobs. These handlers are usually the problem
 * instances themselves as they undergo a state change, such as jobs finishing
 * on machines.
 *
 * ISubscriptionHandler is used in conjunction with @see IProblemInstance to
 * represent the job shop scheduling problem instance.
 *
 * @author parkjohn
 *
 */
public interface ISubscriptionHandler {

	/**
	 * Called when the handler receives subscription request from a subscriber.
	 * @param subscriber The subscriber requesting subscription from the
	 *                   handler.
	 */
	public void onSubscriptionRequest(ISubscriber subscriber);

	/**
	 * Send out a feed to the subscribers that an event occurred for a
	 * particular machine.
	 *
	 * Refer to @see IEvent for the events that can occur for a machine.
	 * @param machine The machine for which the event occurred for.
	 * @param time The time when the machine event occurred.
	 */
	public void sendMachineFeed(IMachine machine, double time);

	/**
	 * Send out a feed to the subscribers that an event occurred for a
	 * particular job.
	 *
	 * Refer to @see IEvent for the events that can occur for a job.
	 * @param job The job for which the event occurred for.
	 * @param time The time when the job event occurred.
	 */
	public void sendJobFeed(IJob job, double time);
}
