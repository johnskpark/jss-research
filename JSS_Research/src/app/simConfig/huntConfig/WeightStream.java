package app.simConfig.huntConfig;

import jasima.core.random.continuous.DblStream;

import java.util.Random;

public class WeightStream extends DblStream {

	private static final long serialVersionUID = 1210269111085133191L;

	private static final double[] WEIGHT = new double[]{1, 2, 4};
	private static final double[] WEIGHT_PROB = new double[]{0.2, 0.8, 1.0};

	private static final int NUM_WEIGHTS = 3;

	private long initSeed;
	private Random rndGen;

	public WeightStream(long seed) {
		super();

		initSeed = seed;
		rndGen = new Random(initSeed);
		setRndGen(rndGen);
	}

	@Override
	public double nextDbl() {
		double rand = getRndGen().nextDouble();

		for (int i = 0; i < NUM_WEIGHTS; i++) {
			if (WEIGHT_PROB[i] > rand) {
				return WEIGHT[i];
			}
		}
		return WEIGHT[NUM_WEIGHTS-1];
	}

}
