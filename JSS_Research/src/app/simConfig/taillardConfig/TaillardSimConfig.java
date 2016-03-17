package app.simConfig.taillardConfig;

import java.util.List;

import app.simConfig.StaticSimConfig;

public class TaillardSimConfig extends StaticSimConfig {

	private static final String DIRECTORY = "dataset/TaillardBenchmarksJobShop/TaillardInsts/";

	private static final String FILE_EXT = ".txt";
	private static final int NUM_INSTANCE_PER_SUBSET = 10;

	private List<String> subsetPrefix;
	private int numConfigs;
	
	public TaillardSimConfig(List<String> subsetPrefix) {
		this.subsetPrefix = subsetPrefix;
		this.numConfigs = subsetPrefix.size() * NUM_INSTANCE_PER_SUBSET;
	}
	
	@Override
	public String getInstFileName(int index) {
		String filePath = String.format("%s%s_%02d%s",
				DIRECTORY,
				subsetPrefix.get(index / NUM_INSTANCE_PER_SUBSET),
				(index % NUM_INSTANCE_PER_SUBSET + 1),
				FILE_EXT);

		return filePath;
	}

	@Override
	public int getNumConfigs() {
		return numConfigs;
	}

	@Override
	public void reset() {
		// Does nothing.
	}

}
