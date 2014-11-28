package jss.problem.dynamic_problem;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import jss.IEvent;
import jss.IEventHandler;
import jss.IJob;
import jss.IMachine;
import jss.ISubscriptionHandler;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class DynamicJob implements IJob, IEventHandler {

	// Immutable component that stays constant during the simulation.
	private List<IMachine> machineList = new LinkedList<IMachine>();

	private Map<IMachine, Double> processingTimes = new HashMap<IMachine, Double>();
	private Map<IMachine, Double> setupTimes = new HashMap<IMachine, Double>();

	private double dueDate = 0;
	private double penalty = 0;
	private double readyTime = 0;

	// Mutable component that is actively modified during the simulation.
	private Queue<IMachine> machineQueue = new LinkedList<IMachine>();

	private IMachine machine;

	private IEvent jobReadyEvent;
	private ISubscriptionHandler subscriptionHandler;

	/**
	 * Generate a new instance of a static job for the static Job Shop
	 * Scheduling problem instance.
	 */
	public DynamicJob(ISubscriptionHandler handler) {
		subscriptionHandler = handler;
		jobReadyEvent = new JobReadyEvent(this);
	}

	/**
	 * Offer the machine as the latest operations that needs to be carried out
	 * for the job to be completed.
	 * @param machine
	 */
	public void offerMachine(IMachine machine) {
		machineList.add(machine);
		machineQueue.offer(machine);
	}

	/**
	 * Set the processing time for the specified machine.
	 * @param machine
	 * @param processing
	 */
	public void setProcessingTime(IMachine machine, double processing) {
		checkMachine(machine);
		processingTimes.put(machine, processing);
	}

	/**
	 * Set the setup time for the specified machine.
	 * @param machine
	 * @param setup
	 */
	public void setSetupTime(IMachine machine, double setup) {
		checkMachine(machine);
		setupTimes.put(machine, setup);
	}

	/**
	 * Set the due date for the specified machine.
	 * @param dueDate
	 */
	public void setDueDate(double dueDate) {
		this.dueDate = dueDate;
	}

	/**
	 * Set the weight/penalty for the specified machine.
	 * @param penalty
	 */
	public void setPenalty(double penalty) {
		this.penalty = penalty;
	}

	/**
	 * Set the release time of the job.
	 * @param release
	 */
	public void setReadyTime(double release) {
		this.readyTime = release;
	}

	// Check invariance.
	private void checkMachine(IMachine machine) {
		if (!machineList.contains(machine)) {
			throw new RuntimeException("Machine has not been offered to the job");
		}
	}

	@Override
	public double getReadyTime() {
		return readyTime;
	}

	@Override
	public double getProcessingTime(IMachine machine) {
		if (processingTimes.containsKey(machine)) {
			return processingTimes.get(machine);
		}
		return 0;
	}

	@Override
	public double getSetupTime(IMachine machine) {
		if (setupTimes.containsKey(machine)) {
			return setupTimes.get(machine);
		}
		return 0;
	}

	@Override
	public double getDueDate() {
		return dueDate;
	}

	@Override
	public double getPenalty() {
		return penalty;
	}

	@Override
	public double getRemainingTime() {
		double remainingTime = 0.0;
		for (IMachine machine : machineQueue) {
			remainingTime += getProcessingTime(machine);
		}
		return remainingTime;
	}

	@Override
	public int getRemainingOperation() {
		return machineQueue.size();
	}

	@Override
	public void startedProcessingOnMachine(IMachine machine) throws RuntimeException {
		if (!machineQueue.peek().equals(machine)) {
			throw new RuntimeException("You done goofed from BasicJob");
		}
		this.machine = machine;
	}

	@Override
	public void finishProcessingOnMachine() {
		machine = null;
		machineQueue.poll();
	}

	@Override
	public IMachine getCurrentMachine() {
		return machine;
	}

	@Override
	public IMachine getNextMachine() {
		if (!machineQueue.isEmpty()) {
			return machineQueue.peek();
		}
		return null;
	}

	@Override
	public IMachine getLastMachine() {
		int diff = machineList.size() - machineQueue.size();
		if (diff != 0) {
			return machineList.get(diff-1);
		}
		return null;
	}

	@Override
	public boolean isProcessable(IMachine machine) {
		return machineQueue.contains(machine);
	}

	@Override
	public boolean isCompleted() {
		return machineQueue.isEmpty();
	}

	@Override
	public void reset() {
		machineQueue = new LinkedList<IMachine>(machineList);
	}


	/**
	 * Update the status of the dynamic job.
	 */
	public void updateStatus() {
		jobReadyEvent = null;
		subscriptionHandler.sendJobFeed(this, readyTime);
	}

	// Dynamic job has event trigger for when the job is released into the market.

	@Override
	public boolean hasEvent() {
		return jobReadyEvent != null;
	}

	@Override
	public IEvent getNextEvent() {
		return jobReadyEvent;
	}

	@Override
	public double getNextEventTime() {
		return readyTime;
	}

	// An event class that represents a job being released into the market.
	private class JobReadyEvent implements IEvent {
		private DynamicJob job;

		public JobReadyEvent(DynamicJob job) {
			this.job = job;
		}

		@Override
		public void trigger() {
			job.updateStatus();
		}

	}

}
