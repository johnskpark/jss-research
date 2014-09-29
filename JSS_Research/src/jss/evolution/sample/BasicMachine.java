package jss.evolution.sample;

import java.util.ArrayList;
import java.util.List;

import jss.IEvent;
import jss.IEventHandler;
import jss.IJob;
import jss.IMachine;
import jss.ISubscriber;
import jss.ISubscriptionHandler;

public class BasicMachine implements IMachine, IEventHandler, ISubscriptionHandler {

	// Mutable components
	private List<IJob> prevJobs = new ArrayList<IJob>();

	private IJob currentJob = null;
	private double availableTime = 0;

	private List<ISubscriber> subscribers = new ArrayList<ISubscriber>();

	public BasicMachine() {
	}

	@Override
	public IJob getCurrentJob() {
		return currentJob;
	}

	@Override
	public IJob getLastProcessedJob() {
		if (prevJobs.isEmpty()) {
			return null;
		}
		return prevJobs.get(prevJobs.size() - 1);
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

		currentJob = job;
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
	public void updateStatus(double time) {
		if (time >= availableTime) {
			prevJobs.add(currentJob);
			currentJob = null;

			sendMachineFeed(this);
		}
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
	public IEvent getNextEvent() {
		if (availableTime == 0) {
			// At the start, the machine triggers an event for something
			// to be processed into the machine.
			return new IEvent() {
				public void trigger() {
					// Do nothing. Its just there at the start for the
					// jobs to start coming in.
				}
			};
		} else {
			// Finish the job.
			return new JobProcessedEvent(this, availableTime);
		}
	}

	@Override
	public double getNextEventTime() {
		return availableTime;
	}

	@Override
	public void onSubscriptionRequest(ISubscriber subscriber) {
		subscribers.add(subscriber);
	}

	@Override
	public void sendMachineFeed(IMachine machine) {
		for (ISubscriber subscriber : subscribers) {
			subscriber.onMachineFeed(machine);
		}
	}

	@Override
	public void sendJobFeed(IJob job) {
		// Do nothing.
	}

	// An event class that represents a job completing on the machine.
	private class JobProcessedEvent implements IEvent {
		private BasicMachine machine;
		private double completionTime;

		public JobProcessedEvent(BasicMachine machine, double time) {
			this.machine = machine;
			this.completionTime = time;
		}

		@Override
		public void trigger() {
			machine.updateStatus(completionTime);
		}
	}
}
