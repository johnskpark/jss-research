package app.simConfig.huntConfig;

import jasima.core.random.continuous.DblStream;

import java.util.Random;

public class TrainDDFStream extends DblStream {

	private static final long serialVersionUID = -2055325666025581558L;

	private static final double[] DUE_DATE_FACTOR = new double[]{3, 5, 7};

	private static final int NUM_FACTORS = 3;

	private long initSeed;
	private Random rndGen;

	public TrainDDFStream(long seed) {
		super();

		initSeed = seed;
		rndGen = new Random(initSeed);
		setRndGen(rndGen);
	}

	@Override
	public double nextDbl() {
		double rand = getRndGen().nextDouble();

		for (int i = 0; i < NUM_FACTORS; i++) {
			if ((i + 1) / 3.0 > rand) {
				return DUE_DATE_FACTOR[i];
			}
		}
		return DUE_DATE_FACTOR[NUM_FACTORS-1];
	}

}
