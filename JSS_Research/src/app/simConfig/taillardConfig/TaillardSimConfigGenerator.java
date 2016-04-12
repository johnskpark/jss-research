package app.simConfig.taillardConfig;

import java.util.ArrayList;
import java.util.List;

import app.simConfig.StaticSimConfig;

public class TaillardSimConfigGenerator {

	private static final String FILE_PREFIX = "tai";

	private static final int[] NUM_JOBS = new int[]{15, 20, 20, 30, 30, 50, 50, 100};
	private static final int[] NUM_MACHINES = new int[]{15, 15, 20, 15, 20, 15, 20, 20};
	private static final int NUM_SUBSETS = 8;

	private static final String[] V_SUBSET_INSTANCES = generateInstanceVar();
	private static final String V_ALL_INSTANCES = "all";

	private TaillardSimConfigGenerator() {
	}

	public static final StaticSimConfig getSimConfig(String instances) {
		List<Integer> jobsList = new ArrayList<Integer>();
		List<Integer> machinesList = new ArrayList<Integer>();
		List<String> subsetsList = new ArrayList<String>();

		if (instances.equals(V_ALL_INSTANCES)) {
			for (int i = 0; i < V_SUBSET_INSTANCES.length; i++) {
				jobsList.add(NUM_JOBS[i]);
				machinesList.add(NUM_MACHINES[i]);
				subsetsList.add(V_SUBSET_INSTANCES[i]);
			}
		} else {
			for (String rawSubsetStr : instances.split(",")) {
				String trimmedSubsetStr = rawSubsetStr.trim();

				int subsetIndex = getSubsetIndex(trimmedSubsetStr);
				if (subsetIndex != -1 && !subsetsList.contains(trimmedSubsetStr)) {
					jobsList.add(subsetIndex);
					machinesList.add(subsetIndex);
					subsetsList.add(trimmedSubsetStr);
				} else {
					throw new RuntimeException("Error in TaillardSimConfigGenerator.");
				}
			}
		}

		return new TaillardSimConfig(jobsList, machinesList, subsetsList);
	}

	public static final int getSubsetIndex(String subsetStr) {
		for (int i = 0; i < V_SUBSET_INSTANCES.length; i++) {
			if (V_SUBSET_INSTANCES[i].equals(subsetStr)) {
				return i;
			}
		}

		return -1;
	}

	private static String[] generateInstanceVar() {
		String[] instanceVar = new String[NUM_SUBSETS];
		for (int i = 0; i < NUM_SUBSETS; i++) {
			instanceVar[i] = String.format("%s%d_%d", FILE_PREFIX, NUM_JOBS[i], NUM_MACHINES[i]);
		}
		return instanceVar;
	}

}
