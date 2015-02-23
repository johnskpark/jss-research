package app.evaluation.priorityRules;

import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.prioRules.basic.ATC;
import app.evaluation.AbsEvalPriorityRule;
import app.evaluation.JasimaEvalConfig;

public class EvalATC extends AbsEvalPriorityRule {

	private static final long serialVersionUID = 374528155611648088L;

	private static final double ATC_K_VALUE = 3.0;

	private PR wrappedRule;

	@Override
	public void setConfiguration(JasimaEvalConfig config) {
		wrappedRule = new ATC(ATC_K_VALUE);
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		return wrappedRule.calcPrio(entry);
	}

}
