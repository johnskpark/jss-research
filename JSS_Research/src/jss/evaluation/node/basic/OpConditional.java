package jss.evaluation.node.basic;

import jss.evaluation.JSSEvalData;
import jss.evaluation.node.INode;
import jss.evaluation.node.NodeAnnotation;
import jss.node.NodeDefinition;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
@NodeAnnotation(node=NodeDefinition.OP_CONDITIONAL)
public class OpConditional implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.OP_CONDITIONAL;

	private INode conditional;
	private INode consequent;
	private INode alternative;

	/**
	 * TODO javadoc.
	 * @param conditional
	 * @param consequent
	 * @param alternative
	 */
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
	public double evaluate(JSSEvalData data) {
		if (conditional.evaluate(data) >= 0) {
			return consequent.evaluate(data);
		} else {
			return alternative.evaluate(data);
		}
	}

}
