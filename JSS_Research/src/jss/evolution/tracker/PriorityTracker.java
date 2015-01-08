package jss.evolution.tracker;

import java.util.ArrayList;
import java.util.List;

import jss.evolution.ITracker;
import ec.Individual;

/**
 * Tracker for all priorities that are assigned to jobs during the simulation, including the jobs
 * that were not selected for processing. This tracker is used for a Cooperative Coevolutionary
 * Ensemble approach that was proposed in a recent paper (TODO cite the paper if it gets
 * accepted).
 *
 * @author parkjohn
 *
 */
public class PriorityTracker implements ITracker {

	private List<List<Double>> allPriorities = new ArrayList<List<Double>>();

	/**
	 * Create a new instance of a priority tracker.
	 */
	public PriorityTracker() {
	}

	/**
	 * Load in the individuals into the priority tracker.
	 * @param inds
	 */
	public void loadIndividuals(Individual[] inds) {
		for (int i = 0; i < inds.length; i++) {
			allPriorities.add(i, new ArrayList<Double>());
		}
	}

	/**
	 * Add the priority to the list of priorities.
	 */
	public void addPriority(int index, double priority) {
		allPriorities.get(index).add(priority);
	}

	/**
	 * Returns the list of all priorities accumulated.
	 */
	protected List<List<Double>> getAllPriorities() {
		return allPriorities;
	}

	/**
	 * Get the list of penalties incurred by the individuals from the
	 * priorities.
	 */
	public List<Double> getPenalties() {
		List<Double> penalties = new ArrayList<Double>();

		double[][] normalisedPriorities = normalisePriorities();

		for (int i = 0; i < allPriorities.size(); i++) {
			penalties.add(calculatePenalty(normalisedPriorities, i));
		}

		return penalties;
	}

	// Normalise the priorities.
	private double[][] normalisePriorities() {
		double[][] normalisedPriorities = new double[allPriorities.size()][];

		for (int i = 0; i < allPriorities.size(); i++) {
			List<Double> priorities = allPriorities.get(i);

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

		for (int i = 0; i < allPriorities.size(); i++) {
			if (index == i) {
				continue;
			}

			for (int j = 0; j < priorities[index].length; j++) {
				penalty += (priorities[index][j] - priorities[i][j]) *
						(priorities[index][j] - priorities[i][j]);
			}
		}

		penalty = 1 - penalty / (priorities[index].length * (allPriorities.size() - 1));
		return penalty;
	}

	/**
	 * Clear the list of priorities.
	 */
	public void clear() {
		for (List<Double> indPriorities : allPriorities) {
			indPriorities.clear();
		}
	}

}
