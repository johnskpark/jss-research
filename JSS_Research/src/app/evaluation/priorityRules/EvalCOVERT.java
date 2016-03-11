package app.evaluation.priorityRules;

import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import app.evaluation.AbsEvalPriorityRule;
import app.evaluation.JasimaEvalConfig;
import app.priorityRules.COVERTPR;

public class EvalCOVERT extends AbsEvalPriorityRule {

	private static final long serialVersionUID = 2205586413073374194L;

	private static final double COVERT_K_VALUE = 3.0;
	private PR pr = new COVERTPR(COVERT_K_VALUE);

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
