package app.evaluation;

import java.util.List;

import app.evaluation.node.INode;

public class JasimaEvalConfig {

	private List<INode> rules;
	private int ruleNum;

	private long seed;

	public void setRules(List<INode> rules) {
		this.rules = rules;
	}

	public void setRuleNum(int ruleNum) {
		this.ruleNum = ruleNum;
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

	public int getRuleNum() {
		return ruleNum;
	}

}
