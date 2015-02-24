package app.node;

import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;

public class CompositePR extends PR {

	private static final long serialVersionUID = -5905946397056301395L;

	private INode node;

	public CompositePR(INode node) {
		this.node = node;
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		return node.evaluate(entry);
	}

}
