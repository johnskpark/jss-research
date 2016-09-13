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

	private WorkStation machine;
	private DowntimeSource downSrc;

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

		// Assume that there is only zero or one breakdown source.
		machine = q.getWorkStation();

		IndividualMachine indMachine = machine.currMachine;
		List<DowntimeSource> srcs = indMachine.getDowntimeSources();
		if (!srcs.isEmpty()) {
			downSrc = srcs.get(0);
		}
	}

	protected boolean addRepairTime(PrioRuleTarget entry, double threshold) {
		if (downSrc == null) {
			return false;
		}

		if (getProbBreakdown(entry) > threshold) {
			return true;
		} else {
			return false;
		}
	}

	protected double getProbBreakdown(PrioRuleTarget entry) {
		if (downSrc == null) {
			return 0.0;
		}

		// Assume that the breakdowns are exponentially distributed.
		DblStream breakdownTimes = downSrc.getTimeBetweenFailures();
		double meanBreakdown = breakdownTimes.getNumericalMean();

		return 1.0 - Math.exp(-entry.getCurrentOperation().procTime / meanBreakdown);
	}

	protected double getMeanRepairTime(PrioRuleTarget entry) {
		if (downSrc == null) {
			return 0.0;
		}

		DblStream repairTimes = downSrc.getTimeToRepair();
		return repairTimes.getNumericalMean();
	}

}
