package jss.evolution.tracker;

import java.util.ArrayList;
import java.util.List;

import ec.Individual;
import jss.evolution.ITracker;

public abstract class PriorityTracker implements ITracker {

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
		allPriorities.clear();

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
	 * Get the list of penalties incurred by the individuals from the
	 * priorities.
	 */
	public abstract List<Double> getPenalties();

	/**
	 * TODO javadoc.
	 * @return
	 */
	protected List<List<Double>> getAllPriorities() {
		return allPriorities;
	}

	public void clear() {
		for (List<Double> indPriorities : allPriorities) {
			indPriorities.clear();
		}
	}
}
