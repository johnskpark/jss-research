package app.evaluation.priorityRules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.evaluation.EvalPriorityRuleBase;
import app.evaluation.JasimaEvalConfig;
import app.node.INode;
import app.node.pr.PRNode;
import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.prioRules.basic.FASFS;

public class EvalFIFO extends EvalPriorityRuleBase {

	private static final long serialVersionUID = 1116972997248208962L;

	private PR pr = new FASFS();

	private List<PrioRuleTarget> entries = new ArrayList<PrioRuleTarget>();
	private Map<PrioRuleTarget, Double> entryPrios = new HashMap<>();

	@Override
	public void setConfiguration(JasimaEvalConfig config) {
	}

	@Override
	public List<INode> getRuleComponents() {
		return Arrays.asList(new INode[]{ new PRNode(pr) });
	}

	@Override
	public void beforeCalc(PriorityQueue<? extends PrioRuleTarget> q) {
		super.beforeCalc(q);
		clear();
		pr.beforeCalc(q);
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		double prio = pr.calcPrio(entry);

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
	}

}
