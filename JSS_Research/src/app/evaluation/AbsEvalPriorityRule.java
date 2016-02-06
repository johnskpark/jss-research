package app.evaluation;

import app.node.NodeData;
import jasima.shopSim.core.PR;

public abstract class AbsEvalPriorityRule extends PR implements IJasimaEvalPriorityRule {

	private static final long serialVersionUID = -4755178527963577302L;

	private long seed;

	private NodeData nodeData;

	// Getters

	public long getSeed() {
		return seed;
	}

	public NodeData getNodeData() {
		return nodeData;
	}

	// Setters

	public void setSeed(long seed) {
		this.seed = seed;
	}

	public void setNodeData(NodeData data) {
		this.nodeData = data;
	}

}
