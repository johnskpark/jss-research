package app.simConfig.fisherConfig;

import app.simConfig.StaticSimConfig;

public class SixBySixSimConfig extends StaticSimConfig {

	private static final int NUM_CONFIGS = 1;

	private static final String directory = "dataset/";

	@Override
	public String getInstFileName(int index) {
		return directory + "js06x06.txt";
	}

	@Override
	public int getNumConfigs() {
		return NUM_CONFIGS;
	}

}
