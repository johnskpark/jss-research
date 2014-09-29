package jss.evolution.sample;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import jss.IEvent;
import jss.IEventHandler;
import jss.IJob;
import jss.IMachine;
import jss.ISubscriber;
import jss.ISubscriptionHandler;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class BasicJob implements IJob, IEventHandler, ISubscriptionHandler {

	// Immutable component TODO more doc
	private List<IMachine> machineList = new LinkedList<IMachine>();

	private Map<IMachine, Double> processingTimes = new HashMap<IMachine, Double>();
	private Map<IMachine, Double> setupTimes = new HashMap<IMachine, Double>();
	private Map<IMachine, Double> dueDates = new HashMap<IMachine, Double>();
	private Map<IMachine, Double> penalties = new HashMap<IMachine, Double>();
	private double releaseTime = 0;

	// Mutable component TODO more doc
	private Queue<IMachine> machineQueue = new LinkedList<IMachine>();

	public BasicJob() {
	}

	/**
	 * TODO javadoc.
	 * @param machine
	 */
	public void offerMachine(IMachine machine) {
		machineList.add(machine);
		machineQueue.offer(machine);
	}

	/**
	 * TODO javadoc.
	 * @param machine
	 * @param processing
	 */
	public void setProcessingTime(IMachine machine, double processing) {
		checkMachine(machine);
		processingTimes.put(machine, processing);
	}

	/**
	 * TODO javadoc.
	 * @param machine
	 * @param setup
	 */
	public void setSetupTime(IMachine machine, double setup) {
		checkMachine(machine);
		setupTimes.put(machine, setup);
	}

	/**
	 * TODO javadoc.
	 * @param machine
	 * @param dueDate
	 */
	public void setDueDate(IMachine machine, double dueDate) {
		checkMachine(machine);
		dueDates.put(machine, dueDate);
	}

	/**
	 * TODO javadoc.
	 * @param machine
	 * @param penalty
	 */
	public void setPenalty(IMachine machine, double penalty) {
		checkMachine(machine);
		penalties.put(machine, penalty);
	}

	/**
	 * TODO javadoc.
	 * @param release
	 */
	public void setReleaseTime(double release) {
		releaseTime = release;
	}

	// Check invariance.
	private void checkMachine(IMachine machine) {
		if (!machineList.contains(machine)) {
			throw new RuntimeException("Machine has not been offered to the job");
		}
	}

	@Override
	public double getReleaseTime() {
		return releaseTime;
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
	public double getDueDate(IMachine machine) {
		if (dueDates.containsKey(machine)) {
			return dueDates.get(machine);
		}
		return 0;
	}

	@Override
	public double getPenalty(IMachine machine) {
		if (penalties.containsKey(machine)) {
			return penalties.get(machine);
		}
		return 1;
	}

	@Override
	public void visitMachine(IMachine machine) throws RuntimeException {
		if (!machineQueue.peek().equals(machine)) {
			throw new RuntimeException("You done goofed from BasicJob");
		}
		machineQueue.poll();
	}

	@Override
	public IMachine getNextMachine() {
		if (!machineQueue.isEmpty()) {
			return machineQueue.peek();
		}
		return null;
	}

	@Override
	public boolean isProcessable(IMachine machine) {
		if (machineQueue.isEmpty()) {
			return false;
		}
		return machineQueue.peek().equals(machine);
	}

	@Override
	public boolean isCompleted() {
		return machineQueue.isEmpty();
	}

	@Override
	public void reset() {
		machineQueue = new LinkedList<IMachine>(machineList);
	}

	// Basic Job has no event triggers.

	@Override
	public boolean hasEvent() {
		return false;
	}

	@Override
	public IEvent getNextEvent() {
		return null;
	}

	@Override
	public double getNextEventTime() {
		return Double.POSITIVE_INFINITY;
	}

	@Override
	public void onSubscriptionRequest(ISubscriber s) {
		// Do nothing.
	}

	@Override
	public void sendMachineFeed(IMachine machine) {
		// Do nothing.
	}

	@Override
	public void sendJobFeed(IJob job) {
		// Do nothing.
	}

}
