package jss;

/**
 * Helper enum that is used by the implemented dataset... TODO javadoc.
 * @author parkjohn
 *
 */
public enum ProblemSize {
	SMALL_PROBLEM_SIZE,
	MEDIUM_PROBLEM_SIZE,
	LARGE_PROBLEM_SIZE;

	/**
	 * TODO javadoc.
	 * @param problemSize
	 * @return
	 */
	public static ProblemSize strToProblemSize(String problemSize) {
		if (problemSize == "small") {
			return ProblemSize.SMALL_PROBLEM_SIZE;
		} else if (problemSize == "medium") {
			return ProblemSize.MEDIUM_PROBLEM_SIZE;
		} else if (problemSize == "large") {
			return ProblemSize.LARGE_PROBLEM_SIZE;
		} else {
			throw new RuntimeException("You done goofed"); // TODO
		}
	}

}
