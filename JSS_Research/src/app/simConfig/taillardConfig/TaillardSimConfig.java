package app.simConfig.taillardConfig;

import app.simConfig.StaticSimConfig;

public class TaillardSimConfig extends StaticSimConfig {

	private static final String DIRECTORY = "dataset/TaillardBenchmarksJobShop/TaillardInsts/";

	private static final String[] SUBSET_PREFIX = new String[] {
			"tai15_15",
			"tai20_15",
			"tai20_20",
//			"tai30_15",
//			"tai30_20",
//			"tai50_15",
//			"tai50_20",
//			"tai100_20",
	};
	private static final String FILE_EXT = ".txt";
	private static final int NUM_INSTANCE_PER_SUBSET = 10;
	private static final int NUM_CONFIGS = SUBSET_PREFIX.length * NUM_INSTANCE_PER_SUBSET;

	@Override
	public String getInstFileName(int index) {
		String filePath = String.format("%s%s_%02d%s",
				DIRECTORY,
				SUBSET_PREFIX[index / NUM_INSTANCE_PER_SUBSET],
				(index % NUM_INSTANCE_PER_SUBSET + 1),
				FILE_EXT);

		return filePath;
	}

	@Override
	public int getNumConfigs() {
		return NUM_CONFIGS;
	}

	@Override
	public void reset() {
		// Does nothing.
	}

}
