package app.node;

import jasima.shopSim.core.PrioRuleTarget;

public interface INode {

	public int getChildrenNum();

	public double evaluate(PrioRuleTarget entry);

}
