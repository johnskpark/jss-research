package app.simConfig.holthausConfig4Repeat;

import java.util.Random;

import jasima.core.random.continuous.DblStream;
import jasima.core.util.Pair;

public class WeightStream extends DblStream {

	private static final long serialVersionUID = 1210269111085133191L;

	private static final double[] WEIGHTS = new double[]{1, 2, 4};
	private static final double[] WEIGHT_PROBS = new double[]{0.2, 0.8, 1.0};

	private int numWeights;
	private double numericalMean;
	private double numericalMin;
	private double numericalMax;

	private long initSeed;
	private Random rndGen;

	public WeightStream(long seed) {
		super();

		numWeights = WEIGHTS.length;

		numericalMean = 0.0;
		numericalMin = Double.POSITIVE_INFINITY;
		numericalMax = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < numWeights; i++) {
			numericalMean += WEIGHTS[i] * WEIGHT_PROBS[i];
			numericalMin = Math.min(numericalMin, WEIGHTS[i]);
			numericalMax = Math.max(numericalMax, WEIGHTS[i]);
		}

		initSeed = seed;
		rndGen = new Random(initSeed);
//		setRndGen(rndGen);
	}

	@Override
	public double nextDbl() {
		double rand = rndGen.nextDouble();

		for (int i = 0; i < numWeights; i++) {
			if (WEIGHT_PROBS[i] > rand) {
				return WEIGHTS[i];
			}
		}
		return WEIGHTS[numWeights-1];
	}

	@Override
	public double getNumericalMean() {
		return numericalMean;
	}

	@Override
	public Pair<Double, Double> getValueRange() {
		return new Pair<Double, Double>(numericalMin, numericalMax);
	}

}
