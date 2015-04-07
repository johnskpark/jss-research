package app.node.basic;

import jasima.shopSim.core.PrioRuleTarget;
import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeDefinition;

@NodeAnnotation(node=NodeDefinition.OP_MAXIMUM)
public class OpMaximum implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.OP_MAXIMUM;

	private INode leftChild;
	private INode rightChild;

	public OpMaximum(INode leftChild, INode rightChild) {
		this.leftChild = leftChild;
		this.rightChild = rightChild;
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(PrioRuleTarget entry) {
		double leftEval = leftChild.evaluate(entry);
		double rightEval = rightChild.evaluate(entry);

		return Math.max(leftEval, rightEval);
	}

}