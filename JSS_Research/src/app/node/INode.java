package app.node;

import jasima.shopSim.core.PrioRuleTarget;

/**
 * TODO javadoc.
 * @author John Park
 *
 */
public interface INode {

	/**
	 * TODO javadoc.
	 * @return
	 */
	public int getChildrenNum();

	/**
	 * TODO javadoc.
	 * @param data TODO
	 * @return
	 */
	public double evaluate(PrioRuleTarget data);
}
