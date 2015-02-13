package app.evaluation.node.basic;

import app.evaluation.node.INode;
import app.evaluation.node.NodeAnnotation;
import app.node.NodeDefinition;
import app.evaluation.JasimaEvalData;

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
	public double evaluate(JasimaEvalData data) {
		if (conditional.evaluate(data) >= 0) {
			return consequent.evaluate(data);
		} else {
			return alternative.evaluate(data);
		}
	}

}
