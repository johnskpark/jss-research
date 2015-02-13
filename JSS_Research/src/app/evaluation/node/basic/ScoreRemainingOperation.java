package app.evaluation.node.basic;

import app.evaluation.JasimaEvalData;
import app.evaluation.node.INode;
import app.evaluation.node.NodeAnnotation;
import app.node.NodeDefinition;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
@NodeAnnotation(node=NodeDefinition.SCORE_REMAINING_OPERATION)
public class ScoreRemainingOperation implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_REMAINING_TIME;

	/**
	 * TODO javadoc.
	 */
	public ScoreRemainingOperation() {
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(JasimaEvalData data) {
		return data.getPrioRuleTarget().numOpsLeft();
	}

}
