package app.node;

import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;

public class CompositePR extends PR {

	private static final long serialVersionUID = -5905946397056301395L;

	private INode node;
	private NodeData data;

	public CompositePR(INode node, NodeData data) {
		this.node = node;
		this.data = data;
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		data.setEntry(entry);

		return node.evaluate(data);
	}

}
