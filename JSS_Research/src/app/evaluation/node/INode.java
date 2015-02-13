package app.evaluation.node;

import app.evaluation.JasimaEvalData;

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
	public double evaluate(JasimaEvalData data);
}
