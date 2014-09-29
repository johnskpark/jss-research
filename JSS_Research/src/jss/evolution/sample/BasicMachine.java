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

	private IEvent machineEvent;

	public BasicMachine() {
		machineEvent = new MachineEvent(this, 0);
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

		job.visitMachine(this);

		currentJob = job;
		availableTime = Math.max(availableTime, job.getReleaseTime()) +
				job.getSetupTime(this) +
				job.getProcessingTime(this);

		machineEvent = new MachineEvent(this, availableTime);
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
			if (availableTime != 0) {
				prevJobs.add(currentJob);
				currentJob = null;
			}

			machineEvent = null;

			sendMachineFeed(this);
		}
	}

	@Override
	public void reset() {
		prevJobs = new ArrayList<IJob>();

		currentJob = null;
		availableTime = 0;

		subscribers = new ArrayList<ISubscriber>(); // TODO let's see if we need this or not.

		machineEvent = new MachineEvent(this, 0);
	}

	@Override
	public boolean hasEvent() {
		return machineEvent != null;
	}

	@Override
	public IEvent getNextEvent() {
		return machineEvent;
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
	private class MachineEvent implements IEvent {
		private BasicMachine machine;
		private double completionTime;

		public MachineEvent(BasicMachine machine, double time) {
			this.machine = machine;
			this.completionTime = time;
		}

		@Override
		public void trigger() {
			machine.updateStatus(completionTime);
		}
	}

}
