package app.tracker.sampler;

import app.tracker.JasimaExperimentTracker;
import jasima.shopSim.core.PR;

public class SamplerFactory {

	public SamplingPR generateSampler(PR refRule, long seed, JasimaExperimentTracker<?> t) {
		return new SamplingPR(refRule, seed, t);
	}

}
