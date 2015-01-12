package jss.node;

/**
 * Enum definitions for the terminals and non-terminals that are used by
 * GP individuals for JSS.
 *
 * @author parkjohn
 *
 */
public enum NodeDefinition {
	OP_ADDITION("+", 2),
	OP_CONDITIONAL("If", 3),
	OP_DIVISION("/", 2),
	OP_MULTIPLICATION("*", 2),
	OP_SUBTRACTION("-", 2),
	OP_MAXIMUM("Max", 2),
	OP_MINIMUM("Min", 2),
	SCORE_LARGE_VALUE("Inf", 0),
	SCORE_DUE_DATE("DD", 0),
	SCORE_PROCESSING_TIME("PR", 0),
	SCORE_JOB_READY_TIME("RJ", 0),
	SCORE_REMAINING_TIME("RT", 0),
	SCORE_SETUP_TIME("S", 0),
	SCORE_REMAINING_OPERATION("RO", 0),
	SCORE_MACHINE_READY_TIME("RM", 0),
	SCORE_NUM_JOBS_WAITING("NJ", 0),
	SCORE_PENALTY("W", 0),
	SCORE_CURRENT_TIME("CT", 0),
	ERC_RANDOM("", 0);

	private String nodeSymbol;
	private int numChildren;

	// Private constructor to ensure that no other classes can access it.
	private NodeDefinition(String nodeSymbol, int numChildren) {
		this.nodeSymbol = nodeSymbol;
		this.numChildren = numChildren;
	}

	@Override
	public String toString() {
		return nodeSymbol;
	}

	public int numChildren() {
		return numChildren;
	}
}
