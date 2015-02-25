package app.node.basic;

import jasima.shopSim.core.PrioRuleTarget;
import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeDefinition;

@NodeAnnotation(node=NodeDefinition.SCORE_NUM_JOBS_WAITING)
public class ScoreNumJobsWaiting implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_NUM_JOBS_WAITING;

	public ScoreNumJobsWaiting() {
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(PrioRuleTarget entry) {
		return entry.getCurrMachine().numJobsWaiting();
	}

}
