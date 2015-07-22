package app.listener.hunt;

import jasima.shopSim.core.PrioRuleTarget;

public class OperationCompletionStat {

	private PrioRuleTarget entry;

	private double arrivalTime;
	private double startTime;
	private double waitTime;
	private double completionTime;

	public OperationCompletionStat(PrioRuleTarget entry) {
		this.entry = entry;
	}

	// Getters

	public PrioRuleTarget getEntry() {
		return entry;
	}

	public double getArrivalTime() {
		return arrivalTime;
	}

	public double getStartTime() {
		return startTime;
	}

	public double getWaitTime() {
		return waitTime;
	}

	public double getCompletionTime() {
		return completionTime;
	}

	// Setters

	public void setArrivalTime(double arrival) {
		this.arrivalTime = arrival;
	}

	public void setStartTime(double start) {
		this.startTime = start;
	}

	public void setWaitTime(double wait) {
		this.waitTime = wait;
	}

	public void setCompletionTime(double completion) {
		this.completionTime = completion;
	}

}
