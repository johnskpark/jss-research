package app.simConfig.fisherConfig;

import app.simConfig.StaticSimConfig;

public class SixBySixSimConfig extends StaticSimConfig {

	private static final int NUM_CONFIGS = 1;

	private static final String directory = "dataset/FisherBenchmarksJobShop/";
	private static final int NUM_JOBS = 6;
	private static final int NUM_MACHINES = 6;

	@Override
	public String getInstFileName(int index) {
		return directory + String.format("js%02dx%02d.txt", NUM_JOBS, NUM_MACHINES);
	}

	@Override
	public int getNumConfigs() {
		return NUM_CONFIGS;
	}

	@Override
	public int getNumMachines(int index) {
		return NUM_MACHINES;
	}

	@Override
	public void reset() {
		// Does nothing.
	}

}
