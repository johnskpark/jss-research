package app.evaluation.priorityRules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.evaluation.AbsEvalPriorityRule;
import app.evaluation.JasimaEvalConfig;
import app.node.INode;
import app.node.NodeData;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

public class BasicPriorityRule extends AbsEvalPriorityRule {

	private static final long serialVersionUID = -4989543026252704190L;
	private static final int RULE_NUM = 1;

	private INode rule;

	private NodeData data;

	private List<PrioRuleTarget> entries = new ArrayList<PrioRuleTarget>();
	private Map<PrioRuleTarget, Double> entryPrios = new HashMap<>();

	@Override
	public void setConfiguration(JasimaEvalConfig config) {
		if (config.getRules().size() != RULE_NUM) {
			throw new RuntimeException("Invalid number of rules: " + config.getRules().size());
		}
		setSeed(config.getSeed());

		this.rule = config.getRules().get(0);
		this.data = config.getNodeData();
	}

	@Override
	public List<INode> getRuleComponents() {
		return Arrays.asList(new INode[]{ rule });
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		super.beforeCalc(q);

		// Basic priority rule does not have diversity measures.
		clear();
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		data.setEntry(entry);

		double prio = rule.evaluate(data);

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
	public List<PrioRuleTarget> getEntryRankings() {
		Collections.sort(entries, new PrioComparator());

		return entries;
	}

	@Override
	public void jobSelected(PrioRuleTarget entry, PriorityQueue<?> q) {
		// Does nothing.
	}

	@Override
	public void clear() {
		entries.clear();
		entryPrios.clear();
	}

	private class PrioComparator implements Comparator<PrioRuleTarget> {
		@Override
		public int compare(PrioRuleTarget o1, PrioRuleTarget o2) {
			double prio1 = entryPrios.get(o1);
			double prio2 = entryPrios.get(o2);

			if (prio1 > prio2) {
				return -1;
			} else if (prio1 < prio2) {
				return 1;
			} else {
				return 0;
			}
		}
	}

}
