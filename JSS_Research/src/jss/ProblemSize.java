package jss;

/**
 * TODO javadoc. Remove this sometime later down the line.
 * Probably make it its own class eh.
 *
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
			throw new RuntimeException("You done goofed");
		}
	}

	// TODO more shit here.

}
