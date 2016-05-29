package app.evaluation.priorityRules;

import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.prioRules.basic.FASFS;

import java.util.Arrays;
import java.util.List;

import app.evaluation.AbsEvalPriorityRule;
import app.evaluation.JasimaEvalConfig;
import app.node.INode;
import app.node.pr.PRNode;

public class EvalFIFO extends AbsEvalPriorityRule {

	private static final long serialVersionUID = 1116972997248208962L;

	private PR pr = new FASFS();

	@Override
	public void setConfiguration(JasimaEvalConfig config) {
	}

	@Override
	public List<INode> getRuleComponents() {
		return Arrays.asList(new INode[]{ new PRNode(pr) });
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
