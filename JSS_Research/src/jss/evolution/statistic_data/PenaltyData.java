package jss.evolution.statistic_data;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class PenaltyData {

	private List<List<Double>> accumulatedPenalties = new ArrayList<List<Double>>();

	/**
	 * TODO javadoc.
	 */
	public PenaltyData() {
	}

	/**
	 * TODO javadoc.
	 * @param penalties
	 */
	public void addPenalties(List<Double> penalties) {
		if (accumulatedPenalties.isEmpty()) {
			for (int i = 0; i < penalties.size(); i++) {
				accumulatedPenalties.add(i, new ArrayList<Double>());
				accumulatedPenalties.get(i).add(0.0);
			}
		}

		for (int i = 0; i < penalties.size(); i++) {
			List<Double> indPenalties = accumulatedPenalties.get(i);

			// Add in the value.
			indPenalties.add(penalties.get(i));

			// Increment the total penalty at the front of the list.
			double sumPenalties = indPenalties.get(0) + penalties.get(i);
			indPenalties.set(0, sumPenalties);
		}
	}

	/**
	 * TODO javadoc.
	 * @param ind
	 * @return
	 */
	public double getAveragePenalty(int index) {
		List<Double> indPenalties = accumulatedPenalties.get(index);
		return indPenalties.get(0) / (indPenalties.size() - 1);
	}

}
