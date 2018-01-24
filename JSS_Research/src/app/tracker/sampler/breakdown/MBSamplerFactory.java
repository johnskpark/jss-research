package app.tracker.sampler.breakdown;

import app.tracker.JasimaExperimentTracker;
import app.tracker.sampler.SamplerFactory;
import app.tracker.sampler.SamplingPR;
import jasima.shopSim.core.PR;

public class MBSamplerFactory extends SamplerFactory {

	@Override
	public <T> SamplingPR<T> generateSampler(PR refRule, long seed, JasimaExperimentTracker<T> t) {
		return new MBSamplingPR<T>(refRule, seed, t);
	}

}
