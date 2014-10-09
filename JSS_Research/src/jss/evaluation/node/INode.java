package jss.evaluation.node;

import jss.evaluation.JSSEvalData;

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
	public double evaluate(JSSEvalData data);
}
