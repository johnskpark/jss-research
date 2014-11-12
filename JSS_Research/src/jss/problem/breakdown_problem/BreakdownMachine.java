package jss.problem.breakdown_problem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jss.IEvent;
import jss.IEventHandler;
import jss.IJob;
import jss.IMachine;
import jss.ISubscriptionHandler;

/**
 * TODO currently just a copy of the static machine. If we don't require
 * the separation, merge the two into one class.
 *
 * @author parkjohn
 *
 */
public class BreakdownMachine implements IMachine, IEventHandler {

	// Mutable components TODO more doc.
	private List<IJob> prevJobs = new ArrayList<IJob>();

	private IJob currentJob = null;
	private double availableTime = 0;

	private IEvent machineEvent;
	private ISubscriptionHandler subscriptionHandler;

	/**
	 * TODO javadoc.
	 */
	public BreakdownMachine(ISubscriptionHandler handler) {
		subscriptionHandler = handler;
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
	public void processJob(IJob job, double time) throws RuntimeException {
		if (currentJob != null || time < availableTime) {
			throw new RuntimeException("You done goofed from BasicMachine");
		}

		job.startedProcessingOnMachine(this);

		currentJob = job;
		availableTime = Math.max(time, job.getReadyTime(this)) +
				job.getSetupTime(this) +
				job.getProcessingTime(this);

		machineEvent = new MachineEvent(this, availableTime);
	}

	@Override
	public boolean isAvailable() {
		return currentJob == null;
	}

	@Override
	public double getReadyTime() {
		return availableTime;
	}

	@Override
	public void updateStatus(double time) {
		if (time >= availableTime) {
			if (availableTime != 0) {
				currentJob.finishProcessingOnMachine();

				prevJobs.add(currentJob);
				currentJob = null;
			}

			machineEvent = null;
			subscriptionHandler.sendMachineFeed(this, time);
		}
	}

	public void breakdown() {
		if (currentJob != null) {
			// TODO do the breakdown.
		}
	}

	@Override
	public void reset() {
		prevJobs = new ArrayList<IJob>();

		currentJob = null;
		availableTime = 0;

		machineEvent = new MachineEvent(this, 0);
	}

	@Override
	public Set<IJob> getWaitingJobs() {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void addWaitingJob(IJob job) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public boolean hasEvent() {
		return machineEvent != null;
	}

	@Override
	public IEvent getNextEvent() {
		// TODO need to return both event if they occur at the same time
		return machineEvent;
	}

	@Override
	public double getNextEventTime() {
		// TODO need to return both event if they occur at the same time
		return availableTime;
	}

	// An event class that represents a job completing on the machine.
	private class MachineEvent implements IEvent {
		private BreakdownMachine machine;
		private double completionTime;

		public MachineEvent(BreakdownMachine machine, double time) {
			this.machine = machine;
			this.completionTime = time;
		}

		@Override
		public void trigger() {
			machine.updateStatus(completionTime);
		}
	}

}
