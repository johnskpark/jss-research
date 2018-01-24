package app.tracker.sampler;

import app.tracker.JasimaExperimentTracker;
import jasima.shopSim.core.PR;

public class SamplerFactory {

	public <T> SamplingPR<T> generateSampler(PR refRule, long seed, JasimaExperimentTracker<T> t) {
		return new SamplingPR<T>(refRule, seed, t);
	}

}
