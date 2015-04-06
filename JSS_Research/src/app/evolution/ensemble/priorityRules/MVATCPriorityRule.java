package app.evolution.ensemble.priorityRules;

import app.priorityRules.ATCPR;

public class MVATCPriorityRule extends MajorityVotingPriorityRule {

	private static final long serialVersionUID = 1341825682701430264L;

	public static final double ATC_K_VALUE = 3.0;

	public MVATCPriorityRule() {
		super();
		setTieBreaker(new ATCPR(ATC_K_VALUE));
	}

}
