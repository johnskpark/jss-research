package app.evolution.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import app.evolution.GroupResult;
import app.evolution.IJasimaTracker;
import ec.gp.GPIndividual;

public class DecisionTracker implements IJasimaTracker {

	private Map<GPIndividual, List<Integer>> decisionMap = new HashMap<GPIndividual, List<Integer>>();

	public void addDecision(GPIndividual ind, int decision) {
		if (!decisionMap.containsKey(ind)) {
			decisionMap.put(ind, new ArrayList<Integer>());
		}
		decisionMap.get(ind).add(decision);
	}

	@Override
	public GroupResult[] getResults() {
		GroupResult[] results = new GroupResult[decisionMap.size()];

		int index = 0;
		for (Map.Entry<GPIndividual, List<Integer>> entry : decisionMap.entrySet()) {
			GPIndividual ind = entry.getKey();
			List<Integer> decisions = entry.getValue();

			double fitness = 0.0;
			for (int decision : decisions) {
				fitness += decision * decision;
			}
			fitness = Math.sqrt(fitness);

			results[index++] = new GroupResult(ind, fitness);
		}

		return results;
	}

	@Override
	public void clear() {
		decisionMap.clear();
	}

}
