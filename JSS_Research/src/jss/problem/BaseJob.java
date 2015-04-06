package jss.problem;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import jss.IEventHandler;
import jss.IJob;
import jss.IMachine;

public abstract class BaseJob implements IJob, IEventHandler {

	// Immutable component that stays constant during the simulation.
	private List<IMachine> machineList = new LinkedList<IMachine>();

	private Map<IMachine, Double> processingTimes = new HashMap<IMachine, Double>();
	private Map<IMachine, Double> setupTimes = new HashMap<IMachine, Double>();

	private double dueDate = 0;
	private double flowFactor = 0;
	private double penalty = 0;
	private double readyTime = 0;

	// Mutable component that is actively modified during the simulation.
	private Queue<IMachine> machineQueue = new LinkedList<IMachine>();

	private IMachine machine;

	private double queueEntryTime = 0;

	private int id;

	/**
	 * Generate a new instance of a static job for the static Job Shop
	 * Scheduling problem instance.
	 */
	public BaseJob(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
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
	 * TODO javadoc.
	 * @param flowFactor
	 */
	public void setFlowFactor(double flowFactor) {
		this.flowFactor = flowFactor;
	}

	/**
	 * Set the release time of the job.
	 * @param release
	 */
	public void setReadyTime(double release) {
		this.readyTime = release;
		this.queueEntryTime = this.readyTime;
	}

	// Check invariance.
	private void checkMachine(IMachine machine) {
		if (!machineList.contains(machine)) {
			throw new RuntimeException("Machine has not been offered to the job");
		}
	}

	/// IJob

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
	public double getProcessingTime(int index) {
		if (index >= 0 && index < processingTimes.size()) {
			return processingTimes.get(machineList.get(index));
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
	public double getSetupTime(int index) {
		if (index >= 0 && index < setupTimes.size()) {
			return setupTimes.get(machineList.get(index));
		}
		return 0;
	}

	@Override
	public double getQueueEntryTime() {
		return queueEntryTime;
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
	public double getFlowFactor() {
		return flowFactor;
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
	public int getRemainingOperations() {
		return machineQueue.size();
	}

	@Override
	public int getNumOperations() {
		return machineList.size();
	}

	@Override
	public void startedProcessingOnMachine(IMachine machine, double time) throws RuntimeException {
		if (!machineQueue.peek().equals(machine)) {
			throw new RuntimeException("Attempted to process job at incorrect machine");
		}
		this.machine = machine;
	}

	@Override
	public void finishProcessingOnMachine() {
		machineQueue.poll();
		if (!machineQueue.isEmpty()) {
			queueEntryTime = machine.getReadyTime();
		}
		machine = null;
	}

	@Override
	public IMachine getProcessingMachine() {
		return machine;
	}

	@Override
	public IMachine getCurrentMachine() {
		if (!machineQueue.isEmpty()) {
			return machineQueue.peek();
		}
		return null;
	}

	@Override
	public IMachine getLastMachine() {
		int index = getLastOperationIndex();
		if (index >= 0) {
			return machineList.get(index);
		}
		return null;
	}

	@Override
	public IMachine getNextMachine() {
		int index = getNextOperationIndex();
		if (index < machineList.size()) {
			return machineList.get(index);
		}
		return null;
	}

	@Override
	public IMachine getMachine(int index) {
		if (index >= 0 && index < machineList.size()) {
			return machineList.get(index);
		}
		return null;
	}

	@Override
	public int getCurrentOperationIndex() {
		return machineList.size() - machineQueue.size();
	}

	@Override
	public int getLastOperationIndex() {
		return machineList.size() - machineQueue.size() - 1;
	}

	@Override
	public int getNextOperationIndex() {
		return machineList.size() - machineQueue.size() + 1;
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
		machineQueue.clear();
		machineQueue.addAll(machineList);

		queueEntryTime = readyTime;
	}

}
