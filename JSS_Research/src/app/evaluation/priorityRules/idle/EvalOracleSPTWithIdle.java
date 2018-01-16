package app.evaluation.priorityRules.idle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.evaluation.JasimaEvalConfig;
import app.node.INode;
import app.node.pr.PRNode;
import jasima.shopSim.core.IdleTime;
import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.prioRules.basic.SPT;

// This SPT knows exactly when the machine will breakdown.
public class EvalOracleSPTWithIdle extends PriorityRuleWithIdleBase {

	private static final long serialVersionUID = -2745028565835365351L;

	private PR spt = new SPT();

	private List<PrioRuleTarget> entries = new ArrayList<>();
	private Map<PrioRuleTarget, Double> entryPrios = new HashMap<>();

	private double idlePrio = Double.NEGATIVE_INFINITY;
	private double idleTime = 0.0;
	private boolean idleSet = false;

	@Override
	public void setConfiguration(JasimaEvalConfig config) {
		super.setConfiguration(config);

		// Does nothing, no need to set configurations.
	}

	@Override
	public List<INode> getRuleComponents() {
		return Arrays.asList(new INode[]{ new PRNode(spt) });
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		super.beforeCalc(q);

		clear();
	}

	@Override
	public double calcIdlePrio(PriorityQueue<?> q) {
		if (!idleSet) {
			calcIdleGeneral(q);
			idleSet = true;
		}

		return idlePrio;
	}

	@Override
	public double calcIdleTime(PriorityQueue<?> q) {
		if (!idleSet) {
			calcIdleGeneral(q);
			idleSet = true;
		}

		return idleTime;
	}

	protected void calcIdleGeneral(PriorityQueue<?> q) {
		double breakdownTime = getNextBreakdown(q.getWorkStation());
		double simTime = q.getWorkStation().shop().simTime();

		boolean nonZeroFilter = false;
		for (int i = 0; i < q.size() && !nonZeroFilter; i++) {
			PrioRuleTarget job = q.get(i);
			if (job instanceof IdleTime) {
				continue;
			}

			if (job.currProcTime() + job.getShop().simTime() < breakdownTime) {
				nonZeroFilter = true;
			}
		}

		if (!nonZeroFilter) {
			idlePrio = Double.POSITIVE_INFINITY;
			idleTime = breakdownTime - simTime;
		}
	}

	@Override
	public double calcJobPrio(PrioRuleTarget entry) {
		double prio = spt.calcPrio(entry);

		entries.add(entry);
		entryPrios.put(entry, prio);

		return prio;
	}

	@Override
	public int getNumRules() {
		return 1;
	}

	@Override
	public int getRuleSize(int index) {
		if (index != 0) {
			throw new ArrayIndexOutOfBoundsException(index);
		}
		return SIZE_NOT_SET;
	}

	@Override
	public void clear() {
		entries.clear();
		entryPrios.clear();

		idlePrio = Double.NEGATIVE_INFINITY;
		idleTime = 0.0;
	}

}
