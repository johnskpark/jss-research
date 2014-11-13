package jss.evolution.solvers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jss.evolution.ITracker;
import ec.Individual;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class PriorityTracker implements ITracker {

	private Map<Individual, Integer> indIndexMap = new HashMap<Individual, Integer>();
	private List<IndividualPrioritiesPair> indList = new ArrayList<IndividualPrioritiesPair>();

	/**
	 * TODO javadoc.
	 */
	public PriorityTracker() {
	}

	/**
	 * TODO javadoc.
	 * @param inds
	 */
	public void loadIndividuals(final Individual[] inds) {
		for (int i = 0; i < inds.length; i++) {
			indIndexMap.put(inds[i], i);

			IndividualPrioritiesPair pair = new IndividualPrioritiesPair();
			pair.ind = inds[i];
			pair.priorities = new ArrayList<Double>();
			indList.add(i, pair);
		}
	}

	/**
	 * TODO javadoc.
	 */
	public void addPriority(Individual ind, double priority) {
		indList.get(indIndexMap.get(ind)).priorities.add(priority);
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	public Map<Individual, Double> getPenalties() {
		Map<Individual, Double> penalties = new HashMap<Individual, Double>();

		double[][] normalisedPriorities = normalisePriorities();

		for (int i = 0; i < indList.size(); i++) {
			penalties.put(indList.get(i).ind, calculatePenalty(normalisedPriorities, i));
		}

		return penalties;
	}

	// Normalise the priorities.
	private double[][] normalisePriorities() {
		double[][] normalisedPriorities = new double[indList.size()][];

		for (int i = 0; i < indList.size(); i++) {
			List<Double> priorities = indList.get(i).priorities;

			normalisedPriorities[i] = new double[priorities.size()];
			for (int j = 0; j < priorities.size(); j++) {
				normalisedPriorities[i][j] = 1.0 / (1.0 + Math.exp(-priorities.get(j)));
			}
		}

		return normalisedPriorities;
	}

	// Calculate the penalty for the particular index.
	private double calculatePenalty(double[][] priorities, int index) {
		double penalty = 0.0;

		for (int i = 0; i < indList.size(); i++) {
			if (index == i) {
				continue;
			}

			for (int j = 0; j < priorities[index].length; j++) {
				penalty += (priorities[index][j] - priorities[i][j]) *
						(priorities[index][j] - priorities[i][j]);
			}
		}

		return (1 - penalty) / (priorities[index].length * (indList.size() - 1));
	}

	/**
	 * TODO javadoc.
	 */
	public void clear() {
		for (IndividualPrioritiesPair pair : indList) {
			pair.priorities.clear();
		}
	}

	private class IndividualPrioritiesPair {
		Individual ind;
		List<Double> priorities;
	}

}
