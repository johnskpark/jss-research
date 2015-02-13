package app.evaluation.node.hildebrandt;

import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;
import app.evaluation.JasimaEvalData;
import app.evaluation.node.INode;
import app.evaluation.node.NodeAnnotation;
import app.node.NodeDefinition;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
@NodeAnnotation(node=NodeDefinition.SCORE_WORK_IN_NEXT_QUEUE)
public class ScoreWorkInNextQueue implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_WORK_IN_NEXT_QUEUE;

	/**
	 * TODO javadoc.
	 */
	public ScoreWorkInNextQueue() {
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(JasimaEvalData data) {
		PrioRuleTarget entry = data.getPrioRuleTarget();

		int nextTask = entry.getTaskNumber() + 1;
		if (nextTask >= entry.numOps()) {
			return 0;
		}
		WorkStation nextMachine = entry.getOps()[nextTask].machine;

		return nextMachine.workContent(false);
	}

}
