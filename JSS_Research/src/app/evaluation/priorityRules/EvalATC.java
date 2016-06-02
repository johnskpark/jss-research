package app.evaluation.priorityRules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.evaluation.AbsEvalPriorityRule;
import app.evaluation.JasimaEvalConfig;
import app.node.INode;
import app.node.pr.PRNode;
import app.priorityRules.ATCPR;
import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

public class EvalATC extends AbsEvalPriorityRule {

	private static final long serialVersionUID = 374528155611648088L;

	private static final double ATC_K_VALUE = 3.0;
	private PR pr = new ATCPR(ATC_K_VALUE);

	private List<PrioRuleTarget> entries = new ArrayList<PrioRuleTarget>();
	private Map<PrioRuleTarget, Double> entryPrios = new HashMap<>();

	@Override
	public void setConfiguration(JasimaEvalConfig config) {
	}

	@Override
	public List<INode> getRuleComponents() {
		return Arrays.asList(new INode[]{ new PRNode(pr) });
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		double prio = pr.calcPrio(entry);

		entries.add(entry);
		entryPrios.put(entry, prio);

		return prio;
	}

	@Override
	public int getNumRules() {
		return 1;
	}

	@Override
	public int getRuleSize(int index) {
		if (index != 0) {
			throw new ArrayIndexOutOfBoundsException(index);
		}
		return SIZE_NOT_SET;
	}

	@Override
	public List<PrioRuleTarget> getEntryRankings() {
		Collections.sort(entries, new PrioComparator());

		return entries;
	}

	@Override
	public void jobSelected(PrioRuleTarget entry, PriorityQueue<?> q) {
		// Does nothing.
	}

	@Override
	public void clear() {
		entries.clear();
		entryPrios.clear();
	}

	private class PrioComparator implements Comparator<PrioRuleTarget> {
		@Override
		public int compare(PrioRuleTarget o1, PrioRuleTarget o2) {
			double prio1 = entryPrios.get(o1);
			double prio2 = entryPrios.get(o2);

			if (prio1 > prio2) {
				return -1;
			} else if (prio1 < prio2) {
				return 1;
			} else {
				return 0;
			}
		}
	}

}
