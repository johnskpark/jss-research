package ec.util;

/**
 * Simple pair class used for utility.
 *
 * @author parkjohn
 *
 * @param <S>
 * @param <T>
 */
public class Pair<S, T> {

	public final S i1;
	public final T i2;

	/**
	 * Construct a new instance of a pair object.
	 * @param i1
	 * @param i2
	 */
	public Pair(S i1, T i2) {
		this.i1 = i1;
		this.i2 = i2;
	}

}
