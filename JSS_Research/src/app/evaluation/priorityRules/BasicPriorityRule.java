package app.evaluation.priorityRules;

import java.util.Arrays;
import java.util.List;

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
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		data.setEntry(entry);

		return rule.evaluate(data);
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

}
