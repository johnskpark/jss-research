package jss.util;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class TalliardRandom implements IRandom {

	private static final int A_CONSTANT = 16807;
	private static final int B_CONSTANT = 127773;
	private static final int C_CONSTANT = 2836;
	private static final int M_CONSTANT = Integer.MAX_VALUE;

	private long currentSeed;
	private long initSeed;

	/**
	 * TODO javadoc.
	 * @param seed
	 */
	public TalliardRandom(long seed) {
		initSeed = seed;
		currentSeed = seed;
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	public long getInitialSeed() {
		return initSeed;
	}

	@Override
	public int nextInt() {
		modifySeed();
		return (int) (currentSeed % M_CONSTANT);
	}

	@Override
	public double nextDouble() {
		modifySeed();
		return 1.0 * currentSeed / M_CONSTANT;
	}

	private void modifySeed() {
		// Refer to the paper "Benchmarks for basic scheduling problems" for the
		// random number generator algorithm.
		long k = (long) Math.floor(1.0 * currentSeed / B_CONSTANT);
		currentSeed = A_CONSTANT * (currentSeed % B_CONSTANT) - k * C_CONSTANT;

		if (currentSeed < 0) {
			currentSeed += M_CONSTANT;
		}
	}

}
