package jss;

/**
 * Exception for when the solver applies an illegal action to the simulation
 * of the JSS problem instances.
 * @author parkjohn
 *
 */
public class IllegalActionException extends RuntimeException {

	private static final long serialVersionUID = -6233156914268156005L;

	/**
	 * Constructs a new illegal action exception with null as its detail message.
	 */
	public IllegalActionException() {
		super();
	}

	/**
	 * Constructs a new illegal action exception with the specified detail message.
	 */
	public IllegalActionException(String message) {
		super(message);
	}

	/**
	 * Constructs a new illegal action exception with the specified detail message and cause.
	 */
	public IllegalActionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new illegal action exception with the specified cause.
	 */
	public IllegalActionException(Throwable cause) {
		super(cause);
	}
}
