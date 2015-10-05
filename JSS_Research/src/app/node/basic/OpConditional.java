package app.node.basic;

import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeData;
import app.node.NodeDefinition;

@NodeAnnotation(node=NodeDefinition.OP_CONDITIONAL)
public class OpConditional implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.OP_CONDITIONAL;

	private INode conditional;
	private INode consequent;
	private INode alternative;

	public OpConditional(INode conditional, INode consequent, INode alternative) {
		this.conditional = conditional;
		this.consequent = consequent;
		this.alternative = alternative;
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(NodeData data) {
		if (conditional.evaluate(data) >= 0) {
			return consequent.evaluate(data);
		} else {
			return alternative.evaluate(data);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || o.getClass() != this.getClass()) {
			return false;
		}
		OpConditional other = (OpConditional) o;
		return this.conditional.equals(other.conditional) &&
				this.consequent.equals(other.consequent) &&
				this.alternative.equals(other.alternative);
	}

}
