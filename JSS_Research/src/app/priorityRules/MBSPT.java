package app.priorityRules;

import java.util.List;

import jasima.core.random.continuous.DblStream;
import jasima.shopSim.core.DowntimeSource;
import jasima.shopSim.core.IndividualMachine;
import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.prioRules.basic.SPT;

public class MBSPT extends PR {

	private PR spt = new SPT();

	private double threshold;
	private DowntimeSource downSrc;

	public MBSPT(double threshold) {
		super();

		this.threshold = threshold;
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		super.beforeCalc(q);

		// Assume that there is only zero or one breakdown source.
		IndividualMachine machine = q.getWorkStation().currMachine;
		List<DowntimeSource> srcs = machine.getDowntimeSources();
		if (!srcs.isEmpty()) {
			downSrc = srcs.get(0);
		}
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		if (addRepairTime(entry)) {
			return spt.calcPrio(entry) + getMeanRepairTime(entry);
		} else {
			return spt.calcPrio(entry);
		}
	}

	private boolean addRepairTime(PrioRuleTarget entry) {
		if (downSrc == null) {
			return false;
		}

		// Assume that the breakdowns are exponentially distributed.
		DblStream breakdownTimes = downSrc.getTimeBetweenFailures();
		// TODO need the last breakdown repair time.

		return false;
	}

	private double getMeanRepairTime(PrioRuleTarget entry) {
		if (downSrc == null) {
			return 0.0;
		}

		DblStream repairTimes = downSrc.getTimeToRepair();
		return repairTimes.getNumericalMean();
	}

}
