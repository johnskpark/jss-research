package app.node.hunt;

import app.listener.hunt.HuntListener;
import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeData;
import app.node.NodeDefinition;

//The average wait time of last five jobs processed all machines on the shop floor.
@NodeAnnotation(node=NodeDefinition.SCORE_AVERAGE_WAIT_TIME_ALL_MACHINE)
public class ScoreAverageWaitTimeAllMachines implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_AVERAGE_WAIT_TIME_ALL_MACHINE;

	public ScoreAverageWaitTimeAllMachines() {
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(NodeData data) {
		HuntListener listener = (HuntListener) data.getWorkStationListener();

		return listener.getAverageWaitTimesAllMachines();
	}

	@Override
	public boolean equals(Object o) {
		return o != null && o.getClass() == this.getClass();
	}

}