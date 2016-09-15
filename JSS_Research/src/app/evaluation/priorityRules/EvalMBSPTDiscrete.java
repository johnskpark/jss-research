package app.evaluation.priorityRules;

import java.util.Arrays;
import java.util.List;

import app.evaluation.EvalPriorityRuleBase;
import app.evaluation.JasimaEvalConfig;
import app.node.INode;
import app.node.pr.PRNode;
import app.priorityRules.MBSPTDiscrete;
import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

public class EvalMBSPTDiscrete extends EvalPriorityRuleBase {

	private static final long serialVersionUID = 6174064107259033769L;

	private static final double THRESHOLD = 0.5;

	private PR pr = null;

	@Override
	public void setConfiguration(JasimaEvalConfig config) {
		pr = new MBSPTDiscrete(THRESHOLD);
	}

	@Override
	public List<INode> getRuleComponents() {
		return Arrays.asList(new INode[]{ new PRNode(pr) });
	}

	@Override
	public void init() {
		super.init();

		pr.init();
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
