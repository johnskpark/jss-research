package jss.evolution.sample;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import jss.problem.IJob;
import jss.problem.IMachine;

public class BasicJob implements IJob {

	// Immutable component
	private List<IMachine> machineList = new LinkedList<IMachine>();
	private Map<IMachine, Double> processingTimes = new HashMap<IMachine, Double>();

	// Mutable component
	private Queue<IMachine> machineQueue = new LinkedList<IMachine>();

	public BasicJob() {
	}

	public void offerMachine(IMachine machine, double processingTime) {
		machineList.add(machine);
		machineQueue.offer(machine);
		processingTimes.put(machine, processingTime);
	}

	@Override
	public double getReleaseTime() {
		return 0;
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
		return 0;
	}

	@Override
	public double getDueDate(IMachine machine) {
		return 0;
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
		return machineQueue.peek().equals(machine);
	}

	@Override
	public void clear() {
		machineQueue = new LinkedList<IMachine>(machineList);
	}
}
