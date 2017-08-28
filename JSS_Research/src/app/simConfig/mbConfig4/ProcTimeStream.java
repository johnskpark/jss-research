package app.simConfig.mbConfig4;

import java.util.Random;

import jasima.core.random.continuous.DblStream;
import jasima.core.util.Pair;

public class ProcTimeStream extends DblStream {

	private static final long serialVersionUID = -5098046816523055169L;

	private double minProcTime;
	private double maxProcTime;

	private long initSeed;
	private Random rndGen;

	public ProcTimeStream(int minProcTime, int maxProcTime, long seed) {
		this.minProcTime = minProcTime;
		this.maxProcTime = maxProcTime;

		initSeed = seed;
		rndGen = new Random(initSeed);
	}

	@Override
	public double nextDbl() {
		return (int) (maxProcTime - minProcTime + 1.0) * rndGen.nextDouble() + minProcTime;
	}

	@Override
	public double getNumericalMean() {
		return (minProcTime + maxProcTime) / 2.0;
	}

	@Override
	public Pair<Double, Double> getValueRange() {
		return new Pair<Double, Double>(minProcTime, maxProcTime);
	}

}
