package app.evaluation.priorityRules;

import java.util.Arrays;
import java.util.List;

import app.evaluation.EvalPriorityRuleBase;
import app.evaluation.JasimaEvalConfig;
import app.node.INode;
import app.node.pr.PRNode;
import app.priorityRules.HolthausRule;
import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

public class EvalHolthausRule extends EvalPriorityRuleBase {

	private static final long serialVersionUID = 6174064107259033769L;

	private PR pr = null;

	@Override
	public void setConfiguration(JasimaEvalConfig config) {
		pr = new HolthausRule();
	}

	@Override
	public void init() {
		super.init();

		pr.init();
	}

	@Override
	public List<INode> getRuleComponents() {
		return Arrays.asList(new INode[]{ new PRNode(pr) });
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		super.beforeCalc(q);

		pr.beforeCalc(q);
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

	@Override
	public void clear() {
		// Does nothing.
	}

}
