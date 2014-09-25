package jss.evolution.sample;

import java.util.ArrayList;
import java.util.List;

import jss.Event;
import jss.EventHandler;
import jss.IJob;
import jss.IMachine;
import jss.Subscriber;
import jss.SubscriptionHandler;

public class BasicMachine implements IMachine, EventHandler, SubscriptionHandler {

	// Mutable components
	private List<IJob> prevJobs = new ArrayList<IJob>();

	private BasicJob currentJob = null;
	private double availableTime = 0;

	private List<Subscriber> subscribers = new ArrayList<Subscriber>();

	public BasicMachine() {
	}

	@Override
	public IJob getCurrentJob() {
		return currentJob;
	}

	@Override
	public List<IJob> getProcessedJobs() {
		return prevJobs;
	}

	@Override
	public void processJob(IJob job) throws RuntimeException {
		if (currentJob != null) {
			throw new RuntimeException("You done goofed from BasicMachine");
		}

		currentJob = (BasicJob)job;
		availableTime = Math.max(availableTime, job.getReleaseTime()) +
				job.getSetupTime(this) +
				job.getProcessingTime(this);
	}

	@Override
	public boolean isAvailable() {
		return currentJob == null;
	}

	@Override
	public double getTimeAvailable() {
		return availableTime;
	}

	@Override
	public void reset() {
		prevJobs = new ArrayList<IJob>();

		currentJob = null;
		availableTime = 0;
	}

	@Override
	public boolean hasEvent() {
		return availableTime == 0 || !isAvailable();
	}

	@Override
	public Event getNextEvent() {
		if (availableTime == 0) {
			// At the start, the machine triggers an event for something
			// to be processed into the machine.
			return new Event() {
				public void trigger() {
					// Do nothing. Its just there at the start for the
					// jobs to start coming in.
				}
			};
		} else {
			// Finish the job.
			return new JobProcessedEvent(this);
		}
	}

	@Override
	public double getNextEventTime() {
		return availableTime;
	}

	@Override
	public void onSubscriptionRequest(Subscriber subscriber) {
		subscribers.add(subscriber);
	}

	@Override
	public void sendMachineFeed(IMachine machine) {
		for (Subscriber subscriber : subscribers) {
			subscriber.onMachineFeed(machine);
		}
	}

	@Override
	public void sendJobFeed(IJob job) {
		// Do nothing.
	}

	// An event class that represents a job completing on the machine.
	private class JobProcessedEvent implements Event {
		private BasicMachine machine;

		public JobProcessedEvent(BasicMachine machine) {
			this.machine = machine;
		}

		@Override
		public void trigger() {
			machine.prevJobs.add(currentJob);
			machine.currentJob = null;

			machine.sendMachineFeed(machine);
		}
	}
}
