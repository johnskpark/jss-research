package app.evaluation.priorityRules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.evaluation.EvalPriorityRuleBase;
import app.evaluation.JasimaEvalConfig;
import app.node.INode;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

public class BasicPriorityRule extends EvalPriorityRuleBase {

	private static final long serialVersionUID = -4989543026252704190L;
	private static final int RULE_NUM = 1;

	private INode rule;

	private List<PrioRuleTarget> entries = new ArrayList<PrioRuleTarget>();
	private Map<PrioRuleTarget, Double> entryPrios = new HashMap<>();

	@Override
	public void setConfiguration(JasimaEvalConfig config) {
		if (config.getRules().size() != RULE_NUM) {
			throw new RuntimeException("Invalid number of rules: " + config.getRules().size());
		}
		setSeed(config.getSeed());
		setNodeData(config.getNodeData());

		this.rule = config.getRules().get(0);
	}

	@Override
	public List<INode> getRuleComponents() {
		return Arrays.asList(new INode[]{ rule });
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		super.beforeCalc(q);

		clear();
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		getNodeData().setEntry(entry);

		double prio = rule.evaluate(getNodeData());

		if (hasTracker()) {
			getTracker().addPriority(this, 0, rule, entry, prio);
		}

		entries.add(entry);
		entryPrios.put(entry, prio);

		return prio;
	}

	@Override
	public String getName() {
		return getClass().getSimpleName() + "[ " + rule.toString() + " ]";
	}

	@Override
	public int getNumRules() {
		return RULE_NUM;
	}

	@Override
	public int getRuleSize(int index) {
		if (index != 0) {
			throw new ArrayIndexOutOfBoundsException(index);
		}
		return rule.getSize();
	}

	@Override
	public void clear() {
		entries.clear();
		entryPrios.clear();
	}

}
