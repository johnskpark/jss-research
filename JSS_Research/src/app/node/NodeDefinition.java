package app.node;

public enum NodeDefinition {

	// Basic node definitions
	OP_ADDITION("+", 2, NodeType.OPERATOR),
	OP_CONDITIONAL("If", 3, NodeType.OPERATOR),
	OP_DIVISION("/", 2, NodeType.OPERATOR),
	OP_MULTIPLICATION("*", 2, NodeType.OPERATOR),
	OP_SUBTRACTION("-", 2, NodeType.OPERATOR),
	OP_MAXIMUM("Max", 2, NodeType.OPERATOR),
	OP_MINIMUM("Min", 2, NodeType.OPERATOR),
	SCORE_LARGE_VALUE("Inf", 0, NodeType.CONSTANT),
	SCORE_DUE_DATE("DD", 0, NodeType.JOB),
	SCORE_PROCESSING_TIME("PR", 0, NodeType.JOB),
	SCORE_OPERATION_READY_TIME("RJ", 0, NodeType.JOB),
	SCORE_REMAINING_TIME("RT", 0, NodeType.JOB),
	SCORE_SETUP_TIME("S", 0, NodeType.JOB),
	SCORE_REMAINING_OPERATION("RO", 0, NodeType.JOB),
	SCORE_MACHINE_READY_TIME("RM", 0, NodeType.MACHINE),
	SCORE_NUM_JOBS_WAITING("NJ", 0, NodeType.JOB),
	SCORE_PENALTY("W", 0, NodeType.JOB),
	SCORE_CURRENT_TIME("CT", 0, NodeType.SHOP),

	// Node definitions from Hunt
	SCORE_NEXT_PROCESSING_TIME("NPT", 0, NodeType.JOB),
	SCORE_NUMBER_WAITING_NEXT_MACHINE("NNQ", 0, NodeType.MACHINE),
	SCORE_AVERAGE_WAIT_TIME_NEXT_MACHINE("NQW", 0, NodeType.MACHINE),
	SCORE_AVERAGE_WAIT_TIME_ALL_MACHINE("AQW", 0, NodeType.MACHINE),

	// Node definitions from Nguyen's R1 representation
	ACTION("", 1, NodeType.OPERATOR),
	DISPATCH("", 0, NodeType.SHOP),
	OP_GREATER_THAN(">", 2, NodeType.OPERATOR),
	OP_LESS_THAN_OR_EQUAL("<=", 2, NodeType.OPERATOR),
	ATTRIBUTE_WORKLOAD_RATIO("WR", 0, NodeType.SHOP),
	ATTRIBUTE_MACHINE_PROGRESS("MP", 0, NodeType.SHOP),
	ATTRIBUTE_DEVIATION_OF_JOBS("DJ", 0, NodeType.SHOP),
	ATTRIBUTE_CRITICAL_MACHINE_IDLENESS("CMI", 0, NodeType.SHOP),
	ATTRIBUTE_CRITICAL_WORKLOAD_RATIO("CWR", 0, NodeType.SHOP),
	ATTRIBUTE_BOTTLENECK_WORKLOAD_RATIO("BWR", 0, NodeType.SHOP),

	SCORE_OPERATIONAL_DUE_DATE("ODD", 0, NodeType.JOB),
	SCORE_SLACK("SL", 0, NodeType.JOB),
	SCORE_TIME_IN_QUEUE("TIQ", 0, NodeType.JOB),
	SCORE_TIME_IN_SYSTEM("TIS", 0, NodeType.JOB),
	SCORE_TIME_TILL_DUE("TTD", 0, NodeType.JOB),
	SCORE_WORK_IN_NEXT_QUEUE("WINQ", 0, NodeType.MACHINE),

	SCORE_AVERAGE_PROCESSING_TIME_IN_QUEUE("APTIQ", 0, NodeType.SHOP),

	ERC_RANDOM("", 0, NodeType.CONSTANT),
	ERC_DISCRETE("", 0, NodeType.CONSTANT),
	ERC_THRESHOLD("", 0, NodeType.CONSTANT),

	// TODO documentation explaining how this thing works.
	DOUBLE_VECTOR("", 0, NodeType.OTHER);

	private String nodeSymbol;
	private int numChildren;
	private NodeType type;

	// Private constructor to ensure that no other classes can access it.
	private NodeDefinition(String nodeSymbol, int numChildren, NodeType type) {
		this.nodeSymbol = nodeSymbol;
		this.numChildren = numChildren;
		this.type = type;
	}

	@Override
	public String toString() {
		return nodeSymbol;
	}

	public int numChildren() {
		return numChildren;
	}

	public NodeType getType() {
		return type;
	}

}
