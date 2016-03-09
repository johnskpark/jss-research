package app.simConfig.huntConfig;

import java.util.Random;

import jasima.core.random.continuous.DblStream;
import jasima.core.util.Pair;

public class ProcTimeStream extends DblStream {

	private static final long serialVersionUID = -5098046816523055169L;
	
	public enum ProcTimeRange {
		LOW_PROC_TIME(1.0, 49.0),
		HIGH_PROC_TIME(1.0, 99.0);
		
		private double minProcTime;
		private double maxProcTime;
		
		private ProcTimeRange(double min, double max) {
			minProcTime = min;
			maxProcTime = max;
		}
	}
	
	private ProcTimeRange procTimeRange;

	private long initSeed;
	private Random rndGen;

	public ProcTimeStream(ProcTimeRange range, long seed) {
		this.procTimeRange = range;
		
		initSeed = seed;
		rndGen = new Random(initSeed);
//		setRndGen(rndGen);
	}

	@Override
	public double nextDbl() {
		return (int) (procTimeRange.maxProcTime - procTimeRange.minProcTime + 1.0) * rndGen.nextDouble() + procTimeRange.minProcTime;
	}

	@Override
	public double getNumericalMean() {
		return (procTimeRange.minProcTime + procTimeRange.maxProcTime) / 2.0;
	}

	@Override
	public Pair<Double, Double> getValueRange() {
		return new Pair<Double, Double>(procTimeRange.minProcTime, procTimeRange.maxProcTime);
	}
	
}
