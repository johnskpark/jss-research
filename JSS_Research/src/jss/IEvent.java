package jss;

/**
 * Represents an event that occurs within the simulation of the Job Shop
 * Scheduling environment. These events are queued by the @see Simulator to be
 * triggered sequentially in the order of the time that we expect these events
 * to occur.
 *
 * Following are the list of possible events that can occur in Job Shop
 * Scheduling environments:
 * - Job is released onto the market.
 * - Job property changes.
 * - Machine finishes processing a job.
 * - Machine breaks down.
 *
 * When further events are added to this program, add to the list of events.
 *
 * @author parkjohn
 *
 */
public interface IEvent {

	/**
	 * Trigger the event.
	 */
	public void trigger();
}
