package jss.evaluation.node.basic;

import jss.IMachine;
import jss.evaluation.JSSEvalData;
import jss.evaluation.node.INode;
import jss.evaluation.node.NodeAnnotation;
import jss.node.NodeDefinition;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
@NodeAnnotation(node=NodeDefinition.SCORE_JOB_READY_TIME)
public class ScoreMachineReadyTime implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_MACHINE_READY_TIME;

	/**
	 * TODO javadoc.
	 */
	public ScoreMachineReadyTime() {
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(JSSEvalData data) {
		IMachine machine = data.getMachine();
		return machine.getTimeAvailable();
	}

}
