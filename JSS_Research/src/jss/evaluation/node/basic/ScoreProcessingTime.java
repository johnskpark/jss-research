package jss.evaluation.node.basic;

import jss.evaluation.JSSEvalData;
import jss.evaluation.node.INode;
import jss.evaluation.node.NodeAnnotation;
import jss.node.NodeDefinition;

@NodeAnnotation(node=NodeDefinition.SCORE_PROCESSING_TIME)
public class ScoreProcessingTime implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_PROCESSING_TIME;

	/**
	 * TODO javadoc.
	 */
	public ScoreProcessingTime() {
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(JSSEvalData data) {
		// TODO Auto-generated method stub
		return 0;
	}

}
