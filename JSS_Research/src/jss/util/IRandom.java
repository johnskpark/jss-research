package jss.util;

/**
 * Generic interface for a random number generator.
 * @author parkjohn
 *
 */
public interface IRandom {

	/**
	 * Get a random integer value between the interval (0, Integer.MAX_VALUE).
	 * @return
	 */
	public int nextInt();

	/**
	 * Get a random double value between the interval (0, 1).
	 * @return
	 */
	public double nextDouble();
}
