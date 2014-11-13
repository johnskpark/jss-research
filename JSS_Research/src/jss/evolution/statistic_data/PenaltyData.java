package jss.evolution.statistic_data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ec.Individual;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class PenaltyData {

	private Map<Individual, List<Double>> accumulatedPenalties = new HashMap<Individual, List<Double>>();

	/**
	 * TODO javadoc.
	 */
	public PenaltyData() {
	}

	/**
	 * TODO javadoc.
	 * @param penalties
	 */
	public void addPenalties(Map<Individual, Double> penalties) {
		for (Map.Entry<Individual, Double> kvp : penalties.entrySet()) {
			// Add in the list if it does not exist yet.
			if (!accumulatedPenalties.containsKey(kvp.getKey())) {
				accumulatedPenalties.put(kvp.getKey(), new ArrayList<Double>());
				accumulatedPenalties.get(kvp.getKey()).add(0.0);
			}

			List<Double> indPenalties = accumulatedPenalties.get(kvp.getKey());

			// Add in the value.
			indPenalties.add(kvp.getValue());

			// Increment the total penalty at the front of the list.
			indPenalties.set(0, indPenalties.get(0) + kvp.getValue());
		}
	}

	/**
	 * TODO javadoc.
	 * @param ind
	 * @return
	 */
	public double getAveragePenalty(Individual ind) {
		List<Double> indPenalties = accumulatedPenalties.get(ind);
		return indPenalties.get(0) / indPenalties.size() - 1;
	}

}
