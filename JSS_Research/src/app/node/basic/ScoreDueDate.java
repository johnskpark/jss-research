package app.node.basic;

import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeData;
import app.node.NodeDefinition;

@NodeAnnotation(node=NodeDefinition.SCORE_DUE_DATE)
public class ScoreDueDate implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_DUE_DATE;

	public ScoreDueDate() {
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(NodeData data) {
		return data.getEntry().getDueDate();
	}

}
