package app.node.pr;

import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeData;
import app.node.NodeDefinition;
import jasima.shopSim.core.PR;

@NodeAnnotation(node=NodeDefinition.PR_NODE)
public class PRNode implements INode {

	private PR pr;

	public PRNode(PR pr) {
		this.pr = pr;
	}

	@Override
	public int getChildrenNum() {
		return 0;
	}

	@Override
	public int getSize() {
		return 1;
	}

	@Override
	public double evaluate(NodeData data) {
		return pr.calcPrio(data.getPrioRuleTarget());
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || o.getClass() != this.getClass()) {
			return false;
		}
		return this.pr.equals(((PRNode) o).pr);
	}

	@Override
	public String toString() {
		return pr.toString() + "";
	}

}
