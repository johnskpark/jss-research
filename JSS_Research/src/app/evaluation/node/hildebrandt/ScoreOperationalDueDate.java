package app.evaluation.node.hildebrandt;

import app.evaluation.JasimaEvalData;
import app.evaluation.node.INode;
import app.evaluation.node.NodeAnnotation;
import app.node.NodeDefinition;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
@NodeAnnotation(node=NodeDefinition.SCORE_OPERATIONAL_DUE_DATE)
public class ScoreOperationalDueDate implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_OPERATIONAL_DUE_DATE;

	/**
	 * TODO javadoc.
	 */
	public ScoreOperationalDueDate() {
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(JasimaEvalData data) {
		return data.getPrioRuleTarget().getCurrentOperationDueDate();
	}

}
