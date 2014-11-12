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

	private static final String SMALL_SIZE_STR = "small";
	private static final String MEDIUM_SIZE_STR = "medium";
	private static final String LARGE_SIZE_STR = "large";

	/**
	 * Convert the specified string to the specified problem size enum.
	 * @throws IllegalArgumentException of the string does not match any of
	 *                                  the problem size enums.
	 */
	public static ProblemSize strToProblemSize(String problemSize) {
		if (SMALL_SIZE_STR.equals(problemSize)) {
			return ProblemSize.SMALL_PROBLEM_SIZE;
		} else if (MEDIUM_SIZE_STR.equals(problemSize)) {
			return ProblemSize.MEDIUM_PROBLEM_SIZE;
		} else if (LARGE_SIZE_STR.equals(problemSize)) {
			return ProblemSize.LARGE_PROBLEM_SIZE;
		} else {
			throw new IllegalArgumentException("Invalid problem size. Must be \"small\", \"medium\" or \"large\". Got " + problemSize);
		}
	}

}
