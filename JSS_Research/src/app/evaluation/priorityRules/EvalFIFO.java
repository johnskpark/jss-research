package app.evaluation.priorityRules;

import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.prioRules.basic.FASFS;
import app.evaluation.AbsEvalPriorityRule;
import app.evaluation.JasimaEvalConfig;

public class EvalFIFO extends AbsEvalPriorityRule {

	private static final long serialVersionUID = 1116972997248208962L;
	
	private PR pr = new FASFS();
	
	@Override
	public void setConfiguration(JasimaEvalConfig config) {
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		return pr.calcPrio(entry);
	}

}
