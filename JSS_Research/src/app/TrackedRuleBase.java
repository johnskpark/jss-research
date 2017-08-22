package app;

import java.util.List;

import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

public abstract class TrackedRuleBase<T> extends PR implements ITrackedRule<T> {

	private static final long serialVersionUID = 2294381566002300842L;

	public abstract List<PrioRuleTarget> getEntryRankings();

	public abstract void jobSelected(PrioRuleTarget entry, PriorityQueue<?> q);

}
