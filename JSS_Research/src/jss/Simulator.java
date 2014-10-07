package jss;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * TODO javadoc here.
 *
 * @author parkjohn
 *
 */
public class Simulator {

	private PriorityQueue<EventGroup> eventQueue = new PriorityQueue<EventGroup>();
	private Map<Double, EventGroup> eventMap = new HashMap<Double, EventGroup>();

	private double currentTime = 0;

	private IProblemInstance problem;

	/**
	 * TODO javadoc.
	 */
	public Simulator(IProblemInstance problem) {
		this.problem = problem;

		for (IEventHandler handler : problem.getEventHandlers()) {
			if (handler.hasEvent()) {
				addEvent(handler.getNextEvent(), handler.getNextEventTime());
			}
		}
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	public double getTime() {
		return currentTime;
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	public boolean hasEvent() {
		return !eventQueue.isEmpty();
	}

	/**
	 * TODO javadoc.
	 */
	public void triggerEvent() {
		EventGroup events = eventQueue.poll();
		eventMap.remove(events.getTime());

		currentTime = events.getTime();

		events.trigger();

		for (IEventHandler handler : problem.getEventHandlers()) {
			if (handler.hasEvent()) {
				addEvent(handler.getNextEvent(), handler.getNextEventTime());
			}
		}
	}

	private void addEvent(IEvent event, double time) {
		if (time < currentTime) {
			throw new RuntimeException(String.format(
					"Attempted to add event at time %.2f before current time %.2f",
					time, currentTime));
		}

		if (!eventMap.containsKey(time)) {
			EventGroup group = new EventGroup(time);

			eventQueue.offer(group);
			eventMap.put(time, group);
		}

		eventMap.get(time).addEvent(event);
	}

	private class EventGroup implements Comparable<EventGroup> {
		private Set<IEvent> eventList = new HashSet<IEvent>();

		private double triggerTime;

		public EventGroup(double triggerTime) {
			this.triggerTime = triggerTime;
		}

		public double getTime() {
			return triggerTime;
		}

		public void addEvent(IEvent event) {
			eventList.add(event);
		}

		public void trigger() {
			for (IEvent event : eventList) {
				event.trigger();
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
