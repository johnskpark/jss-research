package jss;

/**
 * Exception for when the configuration used to setup the solver configuration
 * is malformed and does not meet the preconditions.
 * @author parkjohn
 *
 */
public class IllegalConfigurationException extends RuntimeException {

	private static final long serialVersionUID = 7600722109760644510L;

	/**
	 * Constructs a new illegal configuration exception with null as its detail message.
	 */
	public IllegalConfigurationException() {
		super();
	}

	/**
	 * Constructs a new illegal configuration exception with the specified detail message.
	 */
	public IllegalConfigurationException(String message) {
		super(message);
	}

	/**
	 * Constructs a new illegal configuration exception with the specified detail message and cause.
	 */
	public IllegalConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new illegal configuration exception with the specified cause.
	 */
	public IllegalConfigurationException(Throwable cause) {
		super(cause);
	}

}
