package app.evaluation.priorityRules;

import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.prioRules.basic.EDD;
import app.evaluation.AbsEvalPriorityRule;
import app.evaluation.JasimaEvalConfig;

public class EvalEDD extends AbsEvalPriorityRule {

	private static final long serialVersionUID = -5105454085510968896L;

	private PR pr = new EDD();

	@Override
	public void setConfiguration(JasimaEvalConfig config) {
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		return pr.calcPrio(entry);
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

}
