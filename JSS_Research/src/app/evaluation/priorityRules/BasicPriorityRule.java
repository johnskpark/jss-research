package app.evaluation.priorityRules;

import jasima.shopSim.core.PrioRuleTarget;
import app.evaluation.AbsEvalPriorityRule;
import app.evaluation.JasimaEvalConfig;
import app.node.INode;
import app.node.NodeData;

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
	public double calcPrio(PrioRuleTarget entry) {
		// TODO temporary time keeping.
		long startTime = System.nanoTime();

		data.setEntry(entry);

		double prio = rule.evaluate(data);

		long endTime = System.nanoTime();
		long timeDiff = endTime - startTime;

		System.out.printf("Prio calc: %d\n", timeDiff);


		return prio;
	}

}
