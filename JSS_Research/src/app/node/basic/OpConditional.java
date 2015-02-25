package app.node.basic;

import jasima.shopSim.core.PrioRuleTarget;
import app.node.INode;
import app.node.NodeAnnotation;
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
	public double evaluate(PrioRuleTarget entry) {
		if (conditional.evaluate(entry) >= 0) {
			return consequent.evaluate(entry);
		} else {
			return alternative.evaluate(entry);
		}
	}

}
