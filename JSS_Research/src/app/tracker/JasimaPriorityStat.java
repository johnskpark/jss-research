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

	/**
	 * TODO javadoc.
	 * @param queueLength
	 */
	@SuppressWarnings("unchecked")
	public JasimaPriorityStat(int queueLength) {
		entries = new Pair[queueLength];
		index = 0;
	}

	/**
	 * TODO javadoc.
	 */
	public void addPriority(PrioRuleTarget entry, double priority) {
		entries[index++] = new Pair<PrioRuleTarget, Double>(entry, priority);
	}

	public Pair<PrioRuleTarget, Double>[] getEntries() {
		return entries;
	}

}
