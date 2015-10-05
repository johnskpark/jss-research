package app.node.basic;

import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeData;
import app.node.NodeDefinition;

@NodeAnnotation(node=NodeDefinition.SCORE_PENALTY)
public class ScorePenalty implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_PENALTY;

	public ScorePenalty() {
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(NodeData data) {
		return data.getEntry().getWeight();
	}

}
