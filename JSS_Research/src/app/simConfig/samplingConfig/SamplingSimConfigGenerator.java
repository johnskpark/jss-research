package app.simConfig.samplingConfig;

import app.simConfig.DynamicSimConfig;

public class SamplingSimConfigGenerator {

	private SamplingSimConfigGenerator() {
	}

	public static final DynamicSimConfig getSimConfig() {
		return new SamplingSimConfig();
	}

}
