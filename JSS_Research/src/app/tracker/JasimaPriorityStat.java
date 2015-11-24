package app.tracker;

import jasima.core.util.Pair;
import jasima.shopSim.core.PrioRuleTarget;

public class JasimaPriorityStat {

	private Pair<PrioRuleTarget, Double>[] entries;
	private int index;

	@SuppressWarnings("unchecked")
	public JasimaPriorityStat(int queueLength) {
		entries = new Pair[queueLength];
		index = 0;
	}

	public void add(PrioRuleTarget entry, double priority) {
		entries[index++] = new Pair<PrioRuleTarget, Double>(entry, priority);
	}

	public Pair<PrioRuleTarget, Double>[] getEntries() {
		return entries;
	}

}
