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

	private Map<Individual, List<Double>> indMap = new HashMap<Individual, List<Double>>();

	/**
	 * TODO javadoc.
	 */
	public PriorityTracker() {
	}

	/**
	 * TODO javadoc.
	 * @param inds
	 */
	public void loadIndividuals(Individual[] inds) {
		for (Individual ind : inds) {
			indMap.put(ind, new ArrayList<Double>());
		}
	}

	/**
	 * TODO javadoc.
	 */
	public void addPriority(Individual ind, double priority) {
		indMap.get(ind).add(priority);
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	public Map<Individual, Double> getPenalties() {
		// TODO
		return null;
	}

	/**
	 * TODO javadoc.
	 */
	public void clear() {
		for (Individual ind : indMap.keySet()) {
			indMap.get(ind).clear();
		}
	}

}
