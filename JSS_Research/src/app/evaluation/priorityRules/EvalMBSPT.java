package app.evaluation.priorityRules;

import java.util.Arrays;
import java.util.List;

import app.evaluation.EvalPriorityRuleBase;
import app.evaluation.JasimaEvalConfig;
import app.node.INode;
import app.node.pr.PRNode;
import app.priorityRules.MBSPT;
import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

public class EvalMBSPT extends EvalPriorityRuleBase {

	private static final double THRESHOLD = 0.9;

	private PR pr = new MBSPT(THRESHOLD);

	@Override
	public void setConfiguration(JasimaEvalConfig config) {
		// Does nothing.
	}

	@Override
	public List<INode> getRuleComponents() {
		return Arrays.asList(new INode[]{ new PRNode(pr) });
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		// TODO Auto-generated method stub
		return 0;
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
	public List<PrioRuleTarget> getEntryRankings() {
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	@Override
	public void jobSelected(PrioRuleTarget entry, PriorityQueue<?> q) {
		// Does nothing.
	}

	@Override
	public void clear() {
		// Does nothing.
	}

}
