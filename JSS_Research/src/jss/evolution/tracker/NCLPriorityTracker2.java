package jss.evolution.tracker;

import java.util.ArrayList;
import java.util.List;

public class NCLPriorityTracker2 extends PriorityTracker {

	/**
	 * TODO javadoc.
	 */
	public NCLPriorityTracker2() {
		super();
	}

	public List<Double> getPenalties() {
		List<Double> penalties = new ArrayList<Double>();

		for (int i = 0; i < getAllPriorities().size(); i++) {
			penalties.add(calculatePenalty(i));
		}

		return penalties;
	}

	private double calculatePenalty(int index) {
		List<Double> indexPriorities = getAllPriorities().get(index);
		double penalty = 0.0;

		for (int i = 0; i < indexPriorities.size(); i++) {
			double partialPenalty = 0.0;
			for (int j = 0; j < getAllPriorities().size(); j++) {
				if (index == j) continue;
				partialPenalty += (getAllPriorities().get(j).get(i) - 1);
			}
			penalty += (indexPriorities.get(i) - 1) * partialPenalty /
					(getAllPriorities().size() * indexPriorities.size());
		}

		return penalty;
	}

}
