package jss.problem.static_problem;

import java.util.ArrayList;
import java.util.List;

import jss.IEvent;
import jss.IEventHandler;
import jss.IJob;
import jss.IMachine;
import jss.ISubscriptionHandler;

/**
 * A concrete representation of a machine in a static Job Shop Scheduling
 * problem instances.
 *
 * @see StaticInstance for definition of static Job Shop Scheduling problems.
 *
 * @author parkjohn
 *
 */
public class StaticMachine implements IMachine, IEventHandler {

	// Mutable components to the static machines that is actively modified
	// during the simulation.
	private List<IJob> prevJobs = new ArrayList<IJob>();
	private List<IJob> waitingJobs = new ArrayList<IJob>();

	private IJob currentJob = null;
	private double availableTime = 0;

	private IEvent machineEvent;
	private ISubscriptionHandler subscriptionHandler;

	/**
	 * Generate a new instance of a static machine for the static Job Shop
	 * Scheduling problem instance.
	 * @param handler The subscription handler for callback methods.
	 */
	public StaticMachine(ISubscriptionHandler handler) {
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
		waitingJobs.remove(job);

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

				IMachine nextMachine = currentJob.getNextMachine();
				if (nextMachine != null) {
					nextMachine.addWaitingJob(currentJob);
				}

				prevJobs.add(currentJob);
				currentJob = null;
			}

			machineEvent = null;
			subscriptionHandler.sendMachineFeed(this, time);
		}
	}

	@Override
	public void reset() {
		prevJobs = new ArrayList<IJob>();
		waitingJobs = new ArrayList<IJob>();

		currentJob = null;
		availableTime = 0;

		machineEvent = new MachineEvent(this, 0);
	}

	@Override
	public List<IJob> getWaitingJobs() {
		return waitingJobs;
	}

	@Override
	public void addWaitingJob(IJob job) {
		if (!waitingJobs.contains(job)) {
			waitingJobs.add(job);
		}
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

	// An event class that represents a job completing on the machine.
	private class MachineEvent implements IEvent {
		private StaticMachine machine;
		private double completionTime;

		public MachineEvent(StaticMachine machine, double time) {
			this.machine = machine;
			this.completionTime = time;
		}

		@Override
		public void trigger() {
			machine.updateStatus(completionTime);
		}
	}

}
