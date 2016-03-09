package app.simConfig.huntConfig;

import jasima.core.random.continuous.DblStream;
import jasima.core.util.Pair;

import java.util.Random;

public class DDFStream extends DblStream {

	private static final long serialVersionUID = -4755681412239405023L;

	public enum DDFDefinition { 
		TRAIN(new double[]{3, 5, 7}), 
		TEST(new double[]{2, 4, 6});
		
		private double[] dueDateFactor;
		
		private DDFDefinition(double[] ddf) {
			dueDateFactor = ddf;
		}
	};
	
	private double[] dueDateFactors;
	private int numFactors;
	private double numericalMean;
	private double numericalMin;
	private double numericalMax;

	private long initSeed;
	private Random rndGen;

	public DDFStream(DDFDefinition ddf, long seed) {
		super();

		dueDateFactors = ddf.dueDateFactor;
		numFactors = dueDateFactors.length;
		
		numericalMean = 0.0;
		numericalMin = Double.POSITIVE_INFINITY;
		numericalMax = Double.NEGATIVE_INFINITY;
		for (double dueDateFactor : dueDateFactors) {
			numericalMean += dueDateFactor;
			numericalMin = Math.min(numericalMin, dueDateFactor);
			numericalMax = Math.max(numericalMax, dueDateFactor);
		}
		numericalMean /= numFactors;
		
		initSeed = seed;
		rndGen = new Random(initSeed);
//		setRndGen(rndGen);
	}

	@Override
	public double nextDbl() {
		double rand = rndGen.nextDouble();

		for (int i = 0; i < numFactors; i++) {
			if ((i + 1) / 3.0 > rand) {
				return dueDateFactors[i];
			}
		}
		return dueDateFactors[numFactors-1];
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
