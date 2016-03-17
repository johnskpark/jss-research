package app.simConfig.taillardConfig;

import java.util.ArrayList;
import java.util.List;

import app.simConfig.StaticSimConfig;

public class TaillardSimConfigGenerator {

	private static final String[] V_SUBSET_INSTANCES = new String[] {
			"tai15_15",
			"tai20_15",
			"tai20_20",
			"tai30_15",
			"tai30_20",
			"tai50_15",
			"tai50_20",
			"tai100_20",
	};
	private static final String V_ALL_INSTANCES = "all";
	
	private TaillardSimConfigGenerator() {
	}

	public static final StaticSimConfig getSimConfig(String instances) {
		List<String> subsetsList = new ArrayList<String>();
		
		if (instances.equals(V_ALL_INSTANCES)) {
			for (String subset : V_SUBSET_INSTANCES) {
				subsetsList.add(subset);
			}
		} else {
			for (String rawSubsetStr : instances.split(",")) {
				String trimmedSubsetStr = rawSubsetStr.trim();
				
				if (getSubsetIndex(trimmedSubsetStr) != -1 && !subsetsList.contains(trimmedSubsetStr)) {
					subsetsList.add(trimmedSubsetStr);
				} else {
					throw new RuntimeException("Error in TaillardSimConfigGenerator.");
				}
			}
		}
		
		return new TaillardSimConfig(subsetsList);
	}
	
	public static final int getSubsetIndex(String subsetStr) {
		for (int i = 0; i < V_SUBSET_INSTANCES.length; i++) {
			if (V_SUBSET_INSTANCES[i].equals(subsetStr)) {
				return i;
			}
		}
		
		return -1;
	}

}
