package app.evaluation.priorityRules;

import jasima.shopSim.core.PrioRuleTarget;
import app.evaluation.AbsEvalPriorityRule;
import app.evaluation.JasimaEvalConfig;
import app.evaluation.JasimaEvalData;
import app.evaluation.node.INode;

public class BasicPriorityRule extends AbsEvalPriorityRule {

	private static final long serialVersionUID = -4989543026252704190L;
	private static final int RULE_NUM = 1;

	private INode rule;

	@Override
	public void setConfiguration(JasimaEvalConfig config) {
		if (config.getRules().size() != RULE_NUM) {
			throw new RuntimeException("Invalid number of rules: " + config.getRules().size());
		}
		setSeed(config.getSeed());

		this.rule = config.getRules().get(0);
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		JasimaEvalData data = new JasimaEvalData();
		data.setPrioRuleTarget(entry);

		return rule.evaluate(data);
	}

}
