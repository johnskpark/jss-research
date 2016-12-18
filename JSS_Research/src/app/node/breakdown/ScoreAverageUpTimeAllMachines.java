package app.node.breakdown;

import java.util.Map;

import app.JasimaWorkStationListener;
import app.listener.breakdown.BreakdownListener;
import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeData;
import app.node.NodeDefinition;
import jasima.core.statistics.SummaryStat;

@NodeAnnotation(node=NodeDefinition.SCORE_AVERAGE_UP_TIME_ALL_MACHINES)
public class ScoreAverageUpTimeAllMachines implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_AVERAGE_UP_TIME_ALL_MACHINES;

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
		Map<String, JasimaWorkStationListener> listeners = data.getWorkStationListeners();
		BreakdownListener listener = (BreakdownListener) listeners.get(BreakdownListener.class.getSimpleName());

		if (listener.hasBrokenDownAnyMachine()) {
			SummaryStat upTimeStat = listener.getAllMachineUpTimeStat();

			return upTimeStat.mean();
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
