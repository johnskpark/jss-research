package app.simConfig.huntConfig;

import app.simConfig.DynamicSimConfig;

public class HuntSimConfigGenerator {

	public static final String V_TRAIN_FOUR_OP = "4op";
	public static final String V_TRAIN_EIGHT_OP = "8op";
	public static final String V_TRAIN = "train";
	public static final String V_TEST = "test";

	private HuntSimConfigGenerator() {
	}

	public static final DynamicSimConfig getSimConfig(String instances) {
		if (instances.equals(V_TRAIN_FOUR_OP)) {
			return new FourOpSimConfig();
		} else if (instances.equals(V_TRAIN_EIGHT_OP)) {
			return new EightOpSimConfig();
		} else if (instances.equals(V_TRAIN)) {
			return new TrainSimConfig();
		} else if (instances.equals(V_TEST)) {
			return new TestSimConfig();
		} else {
			return null;
		}
	}

}
