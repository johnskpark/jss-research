package app.evaluation.priorityRules;

import java.util.Arrays;
import java.util.List;

import app.evaluation.AbsEvalPriorityRule;
import app.evaluation.JasimaEvalConfig;
import app.node.INode;
import app.node.pr.PRNode;
import app.priorityRules.ATCPR;
import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;

public class EvalATC extends AbsEvalPriorityRule {

	private static final long serialVersionUID = 374528155611648088L;

	private static final double ATC_K_VALUE = 3.0;
	private PR pr = new ATCPR(ATC_K_VALUE);

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
