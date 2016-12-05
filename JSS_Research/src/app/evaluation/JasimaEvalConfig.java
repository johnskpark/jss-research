package app.evaluation;

import java.util.List;

import app.node.INode;
import app.node.NodeData;

public class JasimaEvalConfig {

	private List<INode> rules;
	private long seed;
	private NodeData data;
	private boolean includeIdleTimes;


	// Setters

	public void setRules(List<INode> rules) {
		this.rules = rules;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	public void setNodeData(NodeData data) {
		this.data = data;
	}

	public void setIncludeIdleTimes(boolean includeIdleTimes) {
		this.includeIdleTimes = includeIdleTimes;
	}


	// Getters

	public long getSeed() {
		return seed;
	}

	public List<INode> getRules() {
		return rules;
	}

	public NodeData getNodeData() {
		return data;
	}

	public boolean getIncludeIdleTimes() {
		return includeIdleTimes;
	}

}
