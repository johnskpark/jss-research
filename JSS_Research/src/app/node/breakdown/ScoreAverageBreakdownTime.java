package app.node.breakdown;

import java.util.Map;

import app.IWorkStationListener;
import app.listener.breakdown.BreakdownListener;
import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeData;
import app.node.NodeDefinition;
import jasima.core.statistics.SummaryStat;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;

@NodeAnnotation(node=NodeDefinition.SCORE_AVERAGE_BREAKDOWN_TIME)
public class ScoreAverageBreakdownTime implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_AVERAGE_BREAKDOWN_TIME;

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public int getSize() {
		return NODE_DEFINITION.numChildren() + 1;
	}

	@Override
	public double evaluate(NodeData data) {
		PrioRuleTarget entry = data.getPrioRuleTarget();

		Map<String, IWorkStationListener> listeners = data.getWorkStationListeners();
		BreakdownListener listener = (BreakdownListener) listeners.get(BreakdownListener.class.getSimpleName());

		WorkStation machine = entry.getCurrMachine();

		if (listener.hasBrokenDown(machine)) {
			SummaryStat breakdownStat = listener.getMachineBreakdownStat(machine);

			return breakdownStat.mean();
		} else {
			return 0.0;
		}
	}

	@Override
	public boolean equals(Object o) {
		return o != null && o.getClass() == this.getClass();
	}

	@Override
	public String toString() {
		return NODE_DEFINITION.toString();
	}

}
