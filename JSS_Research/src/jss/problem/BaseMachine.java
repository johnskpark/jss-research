package jss.problem;

import java.util.ArrayList;
import java.util.List;

import jss.IEvent;
import jss.IEventHandler;
import jss.IJob;
import jss.IMachine;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public abstract class BaseMachine implements IMachine, IEventHandler, Comparable<BaseMachine> {

	// Immutable id that is used to keep track of the machines.
	private int id;

	// Mutable components to the static machines that is actively modified
	// during the simulation.
	private List<IJob> prevJobs = new ArrayList<IJob>();
	private List<IJob> waitingJobs = new ArrayList<IJob>();

	private IJob currentJob = null;
	private double availableTime = 0;

	private IEvent machineEvent;

	private BaseInstance problem;

	/**
	 * TODO javadoc.
	 * @param id
	 * @param problem
	 */
	public BaseMachine(int id, BaseInstance problem) {
		this.id = id;

		this.problem = problem;
		this.machineEvent = new MachineEvent(this, 0);
	}

	/// IMachine

	@Override
	public int getId() {
		return id;
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
		availableTime = Math.max(time, job.getReadyTime()) +
				job.getSetupTime(this) +
				job.getProcessingTime(this);

		machineEvent = new MachineEvent(this, availableTime);
		
		problem.getUnavailableMachines().add(this);
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

				IMachine nextMachine = currentJob.getCurrentMachine();
				if (nextMachine != null) {
					nextMachine.addWaitingJob(currentJob);
				}

				prevJobs.add(currentJob);
				currentJob = null;
			}

			machineEvent = null;

			problem.sendMachineFeed(this, time);
		}
	}

	@Override
	public void reset() {
		prevJobs.clear();
		waitingJobs.clear();

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

	/// IEventHandler

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

	/// Comparable

	@Override
	public int compareTo(BaseMachine other) {
		return this.id - other.id;
	}

	// An event class that represents a job completing on the machine.
	private class MachineEvent implements IEvent {
		private BaseMachine machine;
		private double completionTime;

		public MachineEvent(BaseMachine machine, double time) {
			this.machine = machine;
			this.completionTime = time;
		}

		@Override
		public void trigger() {
			machine.updateStatus(completionTime);
		}
	}

}
