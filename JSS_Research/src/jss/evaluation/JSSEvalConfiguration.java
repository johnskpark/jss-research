package jss.evaluation;

import java.util.List;

import jss.evaluation.node.INode;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class JSSEvalConfiguration {

	private int seed;

	private int ruleNum;
	private List<INode> rules;

	/**
	 * TODO javadoc.
	 */
	public JSSEvalConfiguration() {
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	public int getSeed() {
		return seed;
	}

	/**
	 * TODO javadoc.
	 * @param seed
	 */
	public void setSeed(int seed) {
		this.seed = seed;
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	public int getRuleNum() {
		return ruleNum;
	}

	/**
	 * TODO javadoc.
	 * @param ruleNum
	 */
	public void setRuleNum(int ruleNum) {
		this.ruleNum = ruleNum;
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	public List<INode> getRules() {
		return rules;
	}

	/**
	 * TODO javadoc.
	 * @param rules
	 */
	public void setRules(List<INode> rules) {
		this.rules = rules;
	}

}
