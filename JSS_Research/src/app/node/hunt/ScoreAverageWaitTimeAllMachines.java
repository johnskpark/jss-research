package app.node.hunt;

import java.util.Map;

import app.JasimaWorkStationListener;
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
	public int getSize() {
		return NODE_DEFINITION.numChildren() + 1;
	}

	@Override
	public double evaluate(NodeData data) {
		Map<String, JasimaWorkStationListener> listeners = data.getWorkStationListeners();
		HuntListener listener = (HuntListener) listeners.get(HuntListener.class.getSimpleName());

		return listener.getAverageWaitTimesAllMachines();
	}

	@Override
	public boolean equals(Object o) {
		return o != null && o.getClass() == this.getClass();
	}

	@Override
	public String toString() {
		return NODE_DEFINITION.toString();
	}

}
