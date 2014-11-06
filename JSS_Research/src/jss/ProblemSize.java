package jss;

/**
 * Helper enum that is used by the datasets for the defining the training sets.
 * @author parkjohn
 *
 */
public enum ProblemSize {
	SMALL_PROBLEM_SIZE,
	MEDIUM_PROBLEM_SIZE,
	LARGE_PROBLEM_SIZE;

	/**
	 * Convert the specified string to the specified problem size enum.
	 * @throws IllegalArgumentException of the string does not match any of
	 *                                  the problem size enums.
	 */
	public static ProblemSize strToProblemSize(String problemSize) {
		if (problemSize == "small") {
			return ProblemSize.SMALL_PROBLEM_SIZE;
		} else if (problemSize == "medium") {
			return ProblemSize.MEDIUM_PROBLEM_SIZE;
		} else if (problemSize == "large") {
			return ProblemSize.LARGE_PROBLEM_SIZE;
		} else {
			throw new IllegalArgumentException("Invalid problem size. Must be \"small\", \"medium\" or \"large\". Got " + problemSize);
		}
	}

}
