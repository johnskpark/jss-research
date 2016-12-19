package app.evaluation.priorityRules.idle;

import java.util.List;

import app.evaluation.EvalPriorityRuleBase;
import app.evaluation.JasimaEvalConfig;
import jasima.core.random.continuous.DblStream;
import jasima.shopSim.core.DowntimeSource;
import jasima.shopSim.core.IdleTime;
import jasima.shopSim.core.IndividualMachine;
import jasima.shopSim.core.Operation;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.core.WorkStation;

public abstract class PriorityRuleWithIdleBase extends EvalPriorityRuleBase {

	private static final long serialVersionUID = 2191207420087763065L;

	private boolean includeIdleTimes = false;

	private IdleTime currIdleTime = null;
	private double currIdlePrio = Double.NEGATIVE_INFINITY;

	@Override
	public void setConfiguration(JasimaEvalConfig config) {
		// Just include the idle times.
		includeIdleTimes = config.getIncludeIdleTimes();
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		super.beforeCalc(q);

		clear();

		if (includeIdleTimes && currIdleTime == null) {
			currIdlePrio = calcIdleTime(q);
			currIdleTime = generateIdleTime(q.getWorkStation(), currIdlePrio);

			q.getWorkStation().queue.add(currIdleTime);
		}
	}

	public IdleTime generateIdleTime(WorkStation currMachine, double idleTime) {
		IdleTime idleTimeEntry = new IdleTime(currMachine.shop());

		idleTimeEntry.setArriveTime(currMachine.shop().simTime());
		idleTimeEntry.setRelDate(currMachine.shop().simTime());

		// Let's just worry about procTime and machine for now.
		idleTimeEntry.setOps(new Operation[]{
				new Operation() {{
					machine = currMachine;
					procTime = idleTime;
				}}
		});

		return idleTimeEntry;
	}

	public abstract double calcIdlePrio(PriorityQueue<?> q);

	public abstract double calcIdleTime(PriorityQueue<?> q);

	public abstract double calcJobPrio(PrioRuleTarget entry);

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		if (entry instanceof IdleTime) {
			return currIdlePrio;
		} else {
			return calcJobPrio(entry);
		}
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

	protected double getNextRepairTime(WorkStation machine) {
		DowntimeSource downSrc = getDowntimeSource(machine);
		if (downSrc == null) {
			return 0.0;
		}

		return downSrc.getNextTimeToRepair(); // TODO need to check to make sure that this is correct.
	}

}
