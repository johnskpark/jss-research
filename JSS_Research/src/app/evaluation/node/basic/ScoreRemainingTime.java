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
@NodeAnnotation(node=NodeDefinition.SCORE_REMAINING_TIME)
public class ScoreRemainingTime implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_REMAINING_TIME;

	/**
	 * TODO javadoc.
	 */
	public ScoreRemainingTime() {
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(JasimaEvalData data) {
		return data.getPrioRuleTarget().remainingProcTime();
	}

}
