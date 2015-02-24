package app.evaluation;

import java.util.List;

import app.node.INode;

public class JasimaEvalConfig {

	private List<INode> rules;

	private long seed;

	public void setRules(List<INode> rules) {
		this.rules = rules;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	public long getSeed() {
		return seed;
	}

	public List<INode> getRules() {
		return rules;
	}

}
