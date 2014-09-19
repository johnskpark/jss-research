package jss.event;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * TODO javadoc here.
 *
 * @author parkjohn
 *
 */
public class EventCore {

	private PriorityQueue<EventGroup> eventQueue = new PriorityQueue<EventGroup>();

	private double currentTime = 0;

	/**
	 * TODO
	 */
	public EventCore() {

	}

	public void addEvent(double triggerTime, TimerEvent event) {

	}

	public void pollEvent() {

	}

	private class EventGroup implements Comparable<EventGroup> {
		private List<TimerEvent> eventList = new ArrayList<TimerEvent>();

		private double triggerTime;

		public EventGroup(double triggertime) {
			this.triggerTime = triggerTime;
		}

		public double getTime() {
			return triggerTime;
		}

		public void addEvent(TimerEvent event) {
			eventList.add(event);
		}

		public void trigger() {
			for (TimerEvent event : eventList) {
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
