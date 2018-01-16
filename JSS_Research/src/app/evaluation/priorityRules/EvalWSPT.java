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
import jasima.shopSim.prioRules.weighted.WSPT;

public class EvalWSPT extends EvalPriorityRuleBase {

	private static final long serialVersionUID = 1608472469110269073L;

	private PR pr = new WSPT();

	private List<PrioRuleTarget> entries = new ArrayList<>();
	private Map<PrioRuleTarget, Double> entryPrios = new HashMap<>();

	@Override
	public void setConfiguration(JasimaEvalConfig config) {
		// Does nothing, no need to set configurations.
	}

	@Override
	public List<INode> getRuleComponents() {
		return Arrays.asList(new INode[]{ new PRNode(pr) });
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
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
