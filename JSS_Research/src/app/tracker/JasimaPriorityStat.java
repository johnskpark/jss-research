package app.tracker;

import jasima.core.util.Pair;
import jasima.shopSim.core.PrioRuleTarget;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class JasimaPriorityStat {

	private PrioRuleTarget[] entries;
	private double[] priorities;
	private int index;

	private PrioRuleTarget bestEntry;
	private double bestPriority;

	/**
	 * TODO javadoc.
	 * @param queueLength
	 */
	@SuppressWarnings("unchecked")
	public JasimaPriorityStat(int queueLength) {
		entries = new PrioRuleTarget[queueLength];
		priorities = new double[queueLength];
		index = 0;

		bestEntry = null;
		bestPriority = Double.NEGATIVE_INFINITY;
	}

	/**
	 * TODO javadoc.
	 */
	public void addPriority(PrioRuleTarget entry, double priority) {
		entries[index] = entry;
		priorities[index] = priority;

		index++;

		if (bestPriority < priority) {
			bestEntry = entry;
			bestPriority = priority;
		}
	}

	public PrioRuleTarget[] getEntries() {
		return entries;
	}

	public double[] getPriorities() {
		return priorities;
	}

	public PrioRuleTarget getBestEntry() {
		return bestEntry;
	}

	public double getBestPriority() {
		return bestPriority;
	}

}
