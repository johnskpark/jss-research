package app.evaluation.priorityRules.idle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.evaluation.EvalPriorityRuleBase;
import app.evaluation.JasimaEvalConfig;
import app.jasimaShopSim.core.IdleTime;
import app.node.INode;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.core.WorkStation;

public abstract class PriorityRuleWithIdleBase extends EvalPriorityRuleBase {

	private static final long serialVersionUID = 2191207420087763065L;

	private Map<PrioRuleTarget, Double> jobPrioMap = new HashMap<PrioRuleTarget, Double>();
	private double idlePrio = Double.NEGATIVE_INFINITY;

	// TODO need to find out how long to idle for.
	private boolean includeIdleTimes = false;

	@Override
	public void setConfiguration(JasimaEvalConfig config) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<INode> getRuleComponents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		super.beforeCalc(q);

		clear();

		double maxPrio = Double.NEGATIVE_INFINITY;

		for (int i = 0; i < q.size(); i++) {
			PrioRuleTarget job = q.get(i);
			double jobPrio = calcPrio(job);

			jobPrioMap.put(job, jobPrio);

			if (jobPrio > maxPrio) {
				maxPrio = jobPrio;
			}
		}

		if (includeIdleTimes) {
			idlePrio = calcIdlePrio(q);

			if (idlePrio > maxPrio) {
				double calcIdleTime = calcIdleTime(q);

				PrioRuleTarget idleTime = generateIdleTime(q.getWorkStation(), calcIdleTime);

				// TODO need to put this into the queue somehow.
				// Well fuck I can't seem to add this to the queue because the queue in the workstation is a job.
				// q.add(idleTime);
			}
		}

	}

	// TODO insert the idle time operator here.
	public IdleTime generateIdleTime(WorkStation currMachine, double idleTime) {
		IdleTime idleTimeEntry = new IdleTime(currMachine.shop());

		return idleTimeEntry;
	}

	public abstract double calcIdlePrio(PriorityQueue<?> q);

	public abstract double calcIdleTime(PriorityQueue<?> q);

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumRules() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getRuleSize(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<PrioRuleTarget> getEntryRankings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void jobSelected(PrioRuleTarget entry, PriorityQueue<?> q) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		jobPrioMap.clear();
	}

}
