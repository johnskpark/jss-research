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
@NodeAnnotation(node=NodeDefinition.SCORE_SLACK)
public class ScoreSlack implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_SLACK;

	/**
	 * TODO javadoc.
	 */
	public ScoreSlack() {
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(JasimaEvalData data) {
		PrioRuleTarget entry = data.getPrioRuleTarget();

		return Math.max(entry.getDueDate() - entry.getShop().simTime() - entry.remainingProcTime(), 0);
	}

}
