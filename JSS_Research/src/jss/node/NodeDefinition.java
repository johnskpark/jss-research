package jss.node;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public enum NodeDefinition {
	OP_ADDITION("+", 2),
	OP_CONDITIONAL("If", 3),
	OP_DIVISION("/", 2),
	OP_MULTIPLICATION("*", 2),
	OP_SUBTRACTION("-", 2),
	SCORE_DUE_DATE("D", 0),
	SCORE_LARGE_VALUE("Inf", 0),
	SCORE_PROCESSING_TIME("P", 0),
	SCORE_RELEASE_TIME("R", 0),
	SCORE_REMAINING_TIME("TP", 0),
	SCORE_SETUP_TIME("S", 0);

	private String nodeSymbol;
	private int numChildren;

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
