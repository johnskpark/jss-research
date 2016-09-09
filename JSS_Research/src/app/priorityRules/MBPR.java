package app.priorityRules;

import java.util.List;

import app.listener.breakdown.BreakdownListener;
import jasima.core.random.continuous.DblStream;
import jasima.shopSim.core.DowntimeSource;
import jasima.shopSim.core.IndividualMachine;
import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.core.WorkStation;

public abstract class MBPR extends PR {

	private static final long serialVersionUID = -7828165567635585395L;

	private double threshold;
	private BreakdownListener listener;

	private WorkStation machine;
	private DowntimeSource downSrc;

	public MBPR(double threshold, BreakdownListener listener) {
		super();

		this.threshold = threshold;
		this.listener = listener;
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

	protected boolean addRepairTime(PrioRuleTarget entry) {
		if (downSrc == null) {
			return false;
		}

		// Assume that the breakdowns are exponentially distributed.
		DblStream breakdownTimes = downSrc.getTimeBetweenFailures();
		double meanBreakdown = breakdownTimes.getNumericalMean();

		// Calculate the time when the machine was last repaired.
		double breakdownTime = listener.getMachineBreakdownStat(machine).lastValue();
		double repairTime = listener.getMachineRepairTimeStat(machine).lastValue();
		double availableTime = breakdownTime + repairTime;

		// Calculate the probability of breaking down.
		double time = entry.getShop().simTime() + entry.getCurrentOperation().procTime - availableTime;
		double prob = 1.0 - Math.exp(-meanBreakdown * time);

		if (prob > threshold) {
			return true;
		} else {
			return false;
		}
	}

	protected double getMeanRepairTime(PrioRuleTarget entry) {
		if (downSrc == null) {
			return 0.0;
		}

		DblStream repairTimes = downSrc.getTimeToRepair();
		return repairTimes.getNumericalMean();
	}

}
