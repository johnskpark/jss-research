package app.evaluation.node.hildebrandt;

import jasima.shopSim.core.PrioRuleTarget;
import app.evaluation.JasimaEvalData;
import app.evaluation.node.INode;
import app.evaluation.node.NodeAnnotation;
import app.node.NodeDefinition;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
@NodeAnnotation(node=NodeDefinition.SCORE_NEXT_PROCESSING_TIME)
public class ScoreNextProcessingTime implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_NEXT_PROCESSING_TIME;

	/**
	 * TODO javadoc.
	 */
	public ScoreNextProcessingTime() {
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

		return entry.getOps()[nextTask].procTime;
	}

}
