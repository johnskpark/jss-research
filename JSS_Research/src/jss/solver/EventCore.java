package jss.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import jss.problem.IMachine;
import jss.problem.IProblemInstance;

/**
 * TODO javadoc here.
 *
 * @author parkjohn
 *
 */
public class EventCore {

	private PriorityQueue<EventGroup> eventQueue = new PriorityQueue<EventGroup>();

	private double currentTime = 0;

	private IProblemInstance problem;
	private IRule rule;

	/**
	 * TODO
	 */
	public EventCore(IProblemInstance problem, IRule rule) {
		this.problem = problem;
		this.rule = rule;

		for (IMachine machine : problem.getMachines()) {

		}
	}

	public boolean hasEvent() {
		return !eventQueue.isEmpty();
	}

	public IAction triggerEvent() {
		EventGroup events = eventQueue.poll();
		events.trigger();

		// TODO
		return null;
	}

	private void addEvent() {
		// TODO
	}

	private class EventGroup implements Comparable<EventGroup> {
		private List<Event> eventList = new ArrayList<Event>();

		private double triggerTime;

		public EventGroup(double triggertime) {
			this.triggerTime = triggerTime;
		}

		public double getTime() {
			return triggerTime;
		}

		public void addEvent(Event event) {
			eventList.add(event);
		}

		public void trigger() {
			for (Event event : eventList) {

			}
		}

		@Override
		public int compareTo(EventGroup o) {
			if (this.triggerTime < o.triggerTime) {
				return -1;
			} else if (this.triggerTime > o.triggerTime) {
				return 1;
			} else {
				return 0;
			}
		}
	}
}
