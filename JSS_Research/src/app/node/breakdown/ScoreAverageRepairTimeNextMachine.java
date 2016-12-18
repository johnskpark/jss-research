package app.node.breakdown;

import java.util.Map;

import app.JasimaWorkStationListener;
import app.listener.breakdown.BreakdownListener;
import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeData;
import app.node.NodeDefinition;
import jasima.core.statistics.SummaryStat;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;

@NodeAnnotation(node=NodeDefinition.SCORE_AVERAGE_REPAIR_TIME_NEXT_MACHINE)
public class ScoreAverageRepairTimeNextMachine implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_AVERAGE_REPAIR_TIME_NEXT_MACHINE;

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

		Map<String, JasimaWorkStationListener> listeners = data.getWorkStationListeners();
		BreakdownListener listener = (BreakdownListener) listeners.get(BreakdownListener.class.getSimpleName());

		int nextTask = entry.getTaskNumber() + 1;
		if (nextTask >= entry.numOps()) {
			return 0.0;
		} else {
			WorkStation machine = entry.getOps()[nextTask].machine;

			if (listener.hasBeenRepaired(machine)) {
				SummaryStat repairStat = listener.getMachineRepairTimeStat(machine);

				return repairStat.mean();
			} else {
				return 0.0;
			}
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
