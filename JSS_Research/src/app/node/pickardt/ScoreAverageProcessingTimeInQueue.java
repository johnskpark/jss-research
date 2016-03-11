package app.node.pickardt;

import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeData;
import app.node.NodeDefinition;

@NodeAnnotation(node=NodeDefinition.SCORE_AVERAGE_PROCESSING_TIME_IN_QUEUE)
public class ScoreAverageProcessingTimeInQueue implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_AVERAGE_PROCESSING_TIME_IN_QUEUE;

	public ScoreAverageProcessingTimeInQueue() {
	}

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
		PrioRuleTarget entry = data.getEntry();

		double sumProcTime = 0.0;

		PriorityQueue<?> q = entry.getCurrMachine().queue;
		for (int i = 0; i < q.size(); i++) {
			sumProcTime += q.get(i).currProcTime();
		}

		return sumProcTime / q.size();
	}

}
