package app.tracker;

import jasima.core.util.Pair;
import jasima.shopSim.core.PrioRuleTarget;

import java.util.ArrayList;
import java.util.List;

public class JasimaPriorityStat {

	private List<Pair<PrioRuleTarget, Double>> entries = new ArrayList<Pair<PrioRuleTarget, Double>>();

	public JasimaPriorityStat() {
		// Empty constructor.
	}

	public void add(PrioRuleTarget entry, double priority) {
		entries.add(new Pair<PrioRuleTarget, Double>(entry, priority));
	}

	public List<Pair<PrioRuleTarget, Double>> getEntries() {
		return entries;
	}

}
