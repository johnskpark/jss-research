package app.tracker;

import jasima.core.util.Pair;
import jasima.shopSim.core.PrioRuleTarget;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class JasimaPriorityStat {

	private Pair<PrioRuleTarget, Double>[] entries;
	private int index;

	private PrioRuleTarget bestEntry;
	private double bestPriority;

	/**
	 * TODO javadoc.
	 * @param queueLength
	 */
	@SuppressWarnings("unchecked")
	public JasimaPriorityStat(int queueLength) {
		entries = new Pair[queueLength];
		index = 0;

		bestEntry = null;
		bestPriority = Double.NEGATIVE_INFINITY;
	}

	/**
	 * TODO javadoc.
	 */
	public void addPriority(PrioRuleTarget entry, double priority) {
		entries[index++] = new Pair<PrioRuleTarget, Double>(entry, priority);

		if (bestPriority < priority) {
			bestEntry = entry;
			bestPriority = priority;
		}
	}

	public Pair<PrioRuleTarget, Double>[] getEntries() {
		return entries;
	}

	public PrioRuleTarget getBestEntry() {
		return bestEntry;
	}

	public double getBestPriority() {
		return bestPriority;
	}

}
