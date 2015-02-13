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
@NodeAnnotation(node=NodeDefinition.SCORE_TIME_TILL_DUE)
public class ScoreTimeTillDue implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_TIME_TILL_DUE;

	/**
	 * TODO javadoc.
	 */
	public ScoreTimeTillDue() {
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(JasimaEvalData data) {
		PrioRuleTarget entry = data.getPrioRuleTarget();

		return Math.max(entry.getDueDate() - entry.getShop().simTime(), 0);
	}

}
