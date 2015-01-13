package jss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;

/**
 * Simulates the Job Shop Scheduling environment. The simulator wraps around a
 * particular problem instance, and handles the process of triggering events
 * that occur in the environment, and keeping track of when these events
 * occurred. Refer to @see IEvent for the definitions of events.
 *
 * The Simulator can be considered as an iterator over the list of events that
 * occur over the time it takes to "solve" a Job Shop Scheduling problem
 * instance.
 *
 * To use the Simulator, simply instantiate a new instance of the Simulator,
 * wrapping the problem instance within it. The Simulator will queue the
 * preliminary events that are expected to occur as the simulation starts.
 * Afterwards, loop over the events that occur by first checking whether the
 * Simulator has any events left @see hasEvent, and triggering them if it
 * does (@see triggerEvent).
 *
 * Although the Simulator does not directly link to the
 * @see ISubscriptionHandler, it relies on it to broadcast that an event
 * occurred to all the related parties, such as the @see ISolver.
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
	 * Create a new instance of the simulator.
	 * @param problem The problem instance to simulate.
	 */
	public Simulator(IProblemInstance problem) {
		this.problem = problem;

		problem.initialise();

		for (IEventHandler handler : problem.getEventHandlers()) {
			if (handler.hasEvent()) {
				addEvent(handler.getNextEvent(), handler.getNextEventTime());
			}
		}
	}

	/**
	 * Get the current time of the simulator, which is the time when the last
	 * event was triggered.
	 */
	public double getTime() {
		return currentTime;
	}

	/**
	 * Get whether the simulator has any events left. If the simulator does
	 * not have any more events, the simulation of the Job Shop Scheduling
	 * environment is completed.
	 */
	public boolean hasEvent() {
		return !eventQueue.isEmpty();
	}

	/**
	 * Trigger the next event in the queue.
	 * @throws NoSuchElementException If no more events are queued up in the
	 *                                Simulator, and if the problem instance
	 *                                generates any events that occur before
	 *                                the last triggered event.
	 */
	public void triggerEvent() throws NoSuchElementException {
		if (eventQueue.isEmpty()) {
			throw new NoSuchElementException("Simulator has no more events to trigger");
		}

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

	// Add the next sets of events onto the Simulator queue.
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

	// A group of events that occur at a single point in time.
	private class EventGroup implements Comparable<EventGroup> {
		private List<IEvent> eventList = new ArrayList<IEvent>();

		private double triggerTime;

		public EventGroup(double triggerTime) {
			this.triggerTime = triggerTime;
		}

		public double getTime() {
			return triggerTime;
		}

		public void addEvent(IEvent event) {
			if (!eventList.contains(event)) {
				eventList.add(event);
			}
		}

		public void trigger() {
			for (IEvent event : eventList) {
				// TODO
				long startTime = System.nanoTime();

				event.trigger();

				long endTime = System.nanoTime();
				long timeDiff = endTime - startTime;

				if (timeDiff > 500000) {
					System.out.printf("Time: %dns\n", timeDiff);
				}
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
