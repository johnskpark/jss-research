package app.priorityRules;

import java.util.List;

import jasima.core.random.continuous.DblStream;
import jasima.shopSim.core.DowntimeSource;
import jasima.shopSim.core.IndividualMachine;
import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.core.WorkStation;

public abstract class MBPR extends PR {

	private static final long serialVersionUID = -7828165567635585395L;

	public MBPR() {
		super();
	}

	@Override
	public void init() {
		// Does nothing for now.
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		super.beforeCalc(q);

		// Does nothing.
	}

	protected DowntimeSource getDowntimeSource(WorkStation machine) {
		IndividualMachine indMachine = machine.machDat()[0];

		List<DowntimeSource> srcs = indMachine.getDowntimeSources();
		if (srcs != null && !srcs.isEmpty()) {
			return srcs.get(0);
		} else {
			return null;
		}
	}

	protected boolean addRepairTime(PrioRuleTarget entry, WorkStation machine, double threshold) {
		double prob = getProbBreakdown(entry, machine);

		if (prob > threshold) {
			return true;
		} else {
			return false;
		}
	}

	protected double getProbBreakdown(PrioRuleTarget entry, WorkStation machine) {
		double meanBreakdown = getMeanBreakdown(machine);
		double procTime = entry.getCurrentOperation().procTime;

		// Assume that the breakdowns are exponentially distributed.
		return 1.0 - Math.exp(-procTime / meanBreakdown);
	}

	protected double getMeanBreakdown(WorkStation machine) {
		DowntimeSource downSrc = getDowntimeSource(machine);
		if (downSrc == null) {
			return 0.0;
		}

		DblStream breakdownTimes = downSrc.getTimeBetweenFailures();
		return breakdownTimes.getNumericalMean();
	}

	protected double getMeanRepairTime(WorkStation machine) {
		DowntimeSource downSrc = getDowntimeSource(machine);
		if (downSrc == null) {
			return 0.0;
		}

		DblStream repairTimes = downSrc.getTimeToRepair();
		return repairTimes.getNumericalMean();
	}

	protected double getNextBreakdown(WorkStation machine) {
		DowntimeSource downSrc = getDowntimeSource(machine);
		if (downSrc == null) {
			return Double.POSITIVE_INFINITY;
		}

		return downSrc.getDeactivateTime();
	}

}
