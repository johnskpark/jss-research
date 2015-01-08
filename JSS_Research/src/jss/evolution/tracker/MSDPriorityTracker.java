package jss.evolution.tracker;

import java.util.ArrayList;
import java.util.List;

/**
 * Tracker for all priorities that are assigned to jobs during the simulation, including the jobs
 * that were not selected for processing. This tracker is used for a Cooperative Coevolutionary
 * Ensemble approach that was proposed in a recent paper (TODO cite the paper if it gets
 * accepted).
 *
 * @author parkjohn
 *
 */
public class MSDPriorityTracker extends PriorityTracker {

	public MSDPriorityTracker() {
		super();
	}

	public List<Double> getPenalties() {
		List<Double> penalties = new ArrayList<Double>();

		for (int i = 0; i < getAllPriorities().size(); i++) {
			penalties.add(calculatePenalty(i));
		}

		return penalties;
	}

	// Calculate the penalty for the particular index.
	private double calculatePenalty(int index) {
		List<Double> indexPriorities = getAllPriorities().get(index);
		double penalty = 0.0;

		for (int i = 0; i < getAllPriorities().size(); i++) {
			if (index == i) {
				continue;
			}

			List<Double> iPriorities = getAllPriorities().get(i);

			for (int j = 0; j < indexPriorities.size(); j++) {
				penalty += (indexPriorities.get(j) - iPriorities.get(j)) *
						(indexPriorities.get(j) - iPriorities.get(j));
			}
		}

		penalty = 1 - penalty / (indexPriorities.size() * (getAllPriorities().size() - 1));
		return penalty;
	}

}
