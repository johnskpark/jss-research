package app.evaluation;

import jasima.shopSim.core.PR;

public abstract class AbsEvalPriorityRule extends PR implements IJasimaEvalPriorityRule {

	private static final long serialVersionUID = -4755178527963577302L;

	private long seed;

	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

}
