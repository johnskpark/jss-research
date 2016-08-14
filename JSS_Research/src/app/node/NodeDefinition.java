package app.node;

public enum NodeDefinition {

	// Basic node definitions
	OP_ADDITION("+", 2, NodeType.OPERATOR, NodeValueRange.OPERATOR),
	OP_CONDITIONAL("If", 3, NodeType.OPERATOR, NodeValueRange.OPERATOR),
	OP_DIVISION("/", 2, NodeType.OPERATOR, NodeValueRange.OPERATOR),
	OP_MULTIPLICATION("*", 2, NodeType.OPERATOR, NodeValueRange.OPERATOR),
	OP_SUBTRACTION("-", 2, NodeType.OPERATOR, NodeValueRange.OPERATOR),
	OP_MAXIMUM("Max", 2, NodeType.OPERATOR, NodeValueRange.OPERATOR),
	OP_MINIMUM("Min", 2, NodeType.OPERATOR, NodeValueRange.OPERATOR),
	SCORE_LARGE_VALUE("Inf", 0, NodeType.CONSTANT, NodeValueRange.POSITIVE),
	SCORE_DUE_DATE("DD", 0, NodeType.JOB, NodeValueRange.NON_NEGATIVE),
	SCORE_PROCESSING_TIME("PR", 0, NodeType.JOB, NodeValueRange.NON_NEGATIVE),
	SCORE_OPERATION_READY_TIME("RJ", 0, NodeType.JOB, NodeValueRange.NON_NEGATIVE),
	SCORE_REMAINING_TIME("RT", 0, NodeType.JOB, NodeValueRange.NON_NEGATIVE),
	SCORE_SETUP_TIME("S", 0, NodeType.JOB, NodeValueRange.NON_NEGATIVE),
	SCORE_REMAINING_OPERATION("RO", 0, NodeType.JOB, NodeValueRange.NON_NEGATIVE),
	SCORE_MACHINE_READY_TIME("RM", 0, NodeType.MACHINE, NodeValueRange.NON_NEGATIVE),
	SCORE_NUM_JOBS_WAITING("NJ", 0, NodeType.JOB, NodeValueRange.NON_NEGATIVE),
	SCORE_PENALTY("W", 0, NodeType.JOB, NodeValueRange.NON_NEGATIVE),
	SCORE_CURRENT_TIME("CT", 0, NodeType.SHOP, NodeValueRange.NON_NEGATIVE),

	// Node definitions from Hunt
	SCORE_NEXT_PROCESSING_TIME("NPT", 0, NodeType.JOB, NodeValueRange.NON_NEGATIVE),
	SCORE_NUMBER_WAITING_NEXT_MACHINE("NNQ", 0, NodeType.MACHINE, NodeValueRange.NON_NEGATIVE),
	SCORE_AVERAGE_WAIT_TIME_NEXT_MACHINE("NQW", 0, NodeType.MACHINE, NodeValueRange.NON_NEGATIVE),
	SCORE_AVERAGE_WAIT_TIME_ALL_MACHINE("AQW", 0, NodeType.MACHINE, NodeValueRange.NON_NEGATIVE),

	// Node definitions from Nguyen's R1 representation
	ACTION("", 1, NodeType.OPERATOR, NodeValueRange.OPERATOR),
	DISPATCH("Dispatch", 0, NodeType.SHOP, NodeValueRange.ANY),
	OP_GREATER_THAN(">", 2, NodeType.OPERATOR, NodeValueRange.OPERATOR),
	OP_LESS_THAN_OR_EQUAL("<=", 2, NodeType.OPERATOR, NodeValueRange.OPERATOR),
	ATTRIBUTE_WORKLOAD_RATIO("WR", 0, NodeType.SHOP, NodeValueRange.NON_NEGATIVE),
	ATTRIBUTE_MACHINE_PROGRESS("MP", 0, NodeType.SHOP, NodeValueRange.NON_NEGATIVE),
	ATTRIBUTE_DEVIATION_OF_JOBS("DJ", 0, NodeType.SHOP, NodeValueRange.NON_NEGATIVE),
	ATTRIBUTE_CRITICAL_MACHINE_IDLENESS("CMI", 0, NodeType.SHOP, NodeValueRange.NON_NEGATIVE),
	ATTRIBUTE_CRITICAL_WORKLOAD_RATIO("CWR", 0, NodeType.SHOP, NodeValueRange.NON_NEGATIVE),
	ATTRIBUTE_BOTTLENECK_WORKLOAD_RATIO("BWR", 0, NodeType.SHOP, NodeValueRange.NON_NEGATIVE),

	SCORE_OPERATIONAL_DUE_DATE("ODD", 0, NodeType.JOB, NodeValueRange.NON_NEGATIVE),
	SCORE_SLACK("SL", 0, NodeType.JOB, NodeValueRange.ANY),
	SCORE_TIME_IN_QUEUE("TIQ", 0, NodeType.JOB, NodeValueRange.NON_NEGATIVE),
	SCORE_TIME_IN_SYSTEM("TIS", 0, NodeType.JOB, NodeValueRange.NON_NEGATIVE),
	SCORE_TIME_TILL_DUE("TTD", 0, NodeType.JOB, NodeValueRange.NON_NEGATIVE),
	SCORE_WORK_IN_NEXT_QUEUE("WINQ", 0, NodeType.MACHINE, NodeValueRange.NON_NEGATIVE),

	SCORE_AVERAGE_PROCESSING_TIME_IN_QUEUE("APTIQ", 0, NodeType.SHOP, NodeValueRange.NON_NEGATIVE),

	ERC_RANDOM("", 0, NodeType.CONSTANT, NodeValueRange.ANY),
	ERC_DISCRETE("", 0, NodeType.CONSTANT, NodeValueRange.ANY),
	ERC_THRESHOLD("", 0, NodeType.CONSTANT, NodeValueRange.ANY),

	// Double vector is a unique node type that wraps around a GA individual.
	// Because of this, it has no "name", but outputted as n-tuple doubles.
	DOUBLE_VECTOR("", 0, NodeType.OTHER, NodeValueRange.ANY),

	// Terminals specific to machine breakdown problem.
	SCORE_AVERAGE_BREAKDOWN_TIME_ALL_MACHINES("AABT", 0, NodeType.MACHINE, NodeValueRange.NON_NEGATIVE),
	SCORE_AVERAGE_BREAKDOWN_TIME_NEXT_MACHINE("NABT", 0, NodeType.MACHINE, NodeValueRange.NON_NEGATIVE),
	SCORE_PREVIOUS_BREAKDOWN_TIME_NEXT_MACHINE("NPBT", 0, NodeType.MACHINE, NodeValueRange.NON_NEGATIVE),
	SCORE_AVERAGE_BREAKDOWN_TIME("ABT", 0, NodeType.MACHINE, NodeValueRange.NON_NEGATIVE),
	SCORE_PREVIOUS_BREAKDOWN_TIME("PBT", 0, NodeType.MACHINE, NodeValueRange.NON_NEGATIVE),

	SCORE_AVERAGE_REPAIR_TIME_ALL_MACHINES("AART", 0, NodeType.MACHINE, NodeValueRange.NON_NEGATIVE),
	SCORE_AVERAGE_REPAIR_TIME_NEXT_MACHINE("NART", 0, NodeType.MACHINE, NodeValueRange.NON_NEGATIVE),
	SCORE_PREVIOUS_REPAIR_TIME_NEXT_MACHINE("NPRT", 0, NodeType.MACHINE, NodeValueRange.NON_NEGATIVE),
	SCORE_AVERAGE_REPAIR_TIME("ART", 0, NodeType.MACHINE, NodeValueRange.NON_NEGATIVE),
	SCORE_PREVIOUS_REPAIR_TIME("PRT", 0, NodeType.MACHINE, NodeValueRange.NON_NEGATIVE),

	SCORE_AVERAGE_UP_TIME_ALL_MACHINES("AAUT", 0, NodeType.MACHINE, NodeValueRange.NON_NEGATIVE),
	SCORE_AVERAGE_UP_TIME_NEXT_MACHINE("NAUT", 0, NodeType.MACHINE, NodeValueRange.NON_NEGATIVE),
	SCORE_PREVIOUS_UP_TIME_NEXT_MACHINE("NPUT", 0, NodeType.MACHINE, NodeValueRange.NON_NEGATIVE),
	SCORE_AVERAGE_UP_TIME("AUT", 0, NodeType.MACHINE, NodeValueRange.NON_NEGATIVE),
	SCORE_PREVIOUS_UP_TIME("PUT", 0, NodeType.MACHINE, NodeValueRange.NON_NEGATIVE);

	private String nodeSymbol;
	private int numChildren;
	private NodeType type;
	private NodeValueRange valueRange;

	// Private constructor to ensure that no other classes can access it.
	private NodeDefinition(String nodeSymbol, int numChildren, NodeType type, NodeValueRange valueRange) {
		this.nodeSymbol = nodeSymbol;
		this.numChildren = numChildren;
		this.type = type;
		this.valueRange = valueRange;
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

	public NodeValueRange getValueRange() {
		return valueRange;
	}

}
