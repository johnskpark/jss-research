package app.node.nguyen_r1;

import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeData;
import app.node.NodeDefinition;

@NodeAnnotation(node=NodeDefinition.ACTION)
public class Action implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.ACTION;

	private INode child;

	public Action(INode child) {
		this.child = child;
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public int getSize() {
		return NODE_DEFINITION.numChildren() + 1;
	}

	@Override
	public double evaluate(NodeData data) {
		return child.evaluate(data);
	}

}
