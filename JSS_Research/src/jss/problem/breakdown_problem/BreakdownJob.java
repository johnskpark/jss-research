package jss.problem.breakdown_problem;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import jss.IEvent;
import jss.IEventHandler;
import jss.IJob;
import jss.IMachine;

public class BreakdownJob implements IJob, IEventHandler {

	// Immutable component TODO more doc
	private List<IMachine> machineList = new LinkedList<IMachine>();

	// TODO these will need to change to some classes later on.
	private Map<IMachine, Double> processingTimes = new HashMap<IMachine, Double>();
	private Map<IMachine, Double> setupTimes = new HashMap<IMachine, Double>();
	private Map<IMachine, Double> dueDates = new HashMap<IMachine, Double>();
	private Map<IMachine, Double> penalties = new HashMap<IMachine, Double>();
	private double releaseTime = 0;

	// Mutable component TODO more doc
	private Queue<IMachine> machineQueue = new LinkedList<IMachine>();

	private IMachine machine;

	public BreakdownJob() {
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
	public double getReadyTime() {
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
	public double getDueDate() {
		// TODO
		return 0;
	}

	@Override
	public double getPenalty() {
		// TODO
		return 1;
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

}