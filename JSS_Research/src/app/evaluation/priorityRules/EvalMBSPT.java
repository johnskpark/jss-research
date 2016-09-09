package app.evaluation.priorityRules;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import app.IWorkStationListener;
import app.evaluation.EvalPriorityRuleBase;
import app.evaluation.JasimaEvalConfig;
import app.listener.breakdown.BreakdownListener;
import app.node.INode;
import app.node.NodeData;
import app.node.pr.PRNode;
import app.priorityRules.MBSPT;
import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

public class EvalMBSPT extends EvalPriorityRuleBase {

	private static final long serialVersionUID = 6174064107259033769L;

	private static final double THRESHOLD = 0.9;

	private PR pr = null;

	@Override
	public void setConfiguration(JasimaEvalConfig config) {
		NodeData data = config.getNodeData();
		Map<String, IWorkStationListener> listeners = data.getWorkStationListeners();

		BreakdownListener listener = (BreakdownListener) listeners.get(BreakdownListener.class.getSimpleName());
		pr = new MBSPT(THRESHOLD, listener);
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
