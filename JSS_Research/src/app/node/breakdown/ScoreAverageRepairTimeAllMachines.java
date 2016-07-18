package app.node.breakdown;

import java.util.Map;

import app.IWorkStationListener;
import app.listener.breakdown.BreakdownListener;
import app.node.INode;
import app.node.NodeData;
import app.node.NodeDefinition;
import jasima.core.statistics.SummaryStat;

public class ScoreAverageRepairTimeAllMachines implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_AVERAGE_BREAKDOWN_TIME_ALL_MACHINES;

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
		Map<String, IWorkStationListener> listeners = data.getWorkStationListener();
		BreakdownListener listener = (BreakdownListener) listeners.get(BreakdownListener.class.getSimpleName());

		if (listener.hasBrokenDownAnyMachine()) {
			SummaryStat repairStat = listener.getAllMachineRepairStat();

			return repairStat.mean();
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
