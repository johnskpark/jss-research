package jss;

/**
 * Interface for objects that trigger events in the EventCore.
 *
 * TODO more description here. Also, make sure to change the name from
 * "object" to something else (and also rename the class while you're at
 * it).
 *
 * @author parkjohn
 *
 */
public interface IEventHandler {

	/**
	 * Returns whether the object has events or not.
	 * @return false if no more events are to be triggered by the object.
	 */
	public boolean hasEvent();

	/**
	 * Returns the next event to be queued up by the EventCore.
	 * @return null if no more events are to be triggered by the object.
	 */
	public IEvent getNextEvent();

	/**
	 * Returns the time when the next event is expected to be triggered
	 * for its priority within the EventCore TODO this is not clear at
	 * all
	 * @return infinity of no more events are to be triggered by the object.
	 */
	public double getNextEventTime();
}
