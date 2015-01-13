package jss.problem.static_problem;

import jss.IEvent;
import jss.problem.BaseJob;

/**
 * A concrete representation of a job in a static Job Shop Scheduling problem
 * instances.
 *
 * @see StaticInstance for definition of static Job Shop Scheduling problems.
 *
 * @author parkjohn
 *
 */
public class StaticJob extends BaseJob {

	/**
	 * Generate a new instance of a static job for the static Job Shop
	 * Scheduling problem instance.
	 */
	public StaticJob() {
		super();
	}

	/// IEventHandler

	@Override
	public boolean hasEvent() {
		return false;
	}

	@Override
	public IEvent getNextEvent() {
		return null;
	}

	@Override
	public double getNextEventTime() {
		return Double.POSITIVE_INFINITY;
	}

}
