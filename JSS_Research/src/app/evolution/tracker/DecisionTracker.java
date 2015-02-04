package app.evolution.tracker;

import jasima.core.statistics.SummaryStat;
import app.evolution.IJasimaTracker;

public class DecisionTracker implements IJasimaTracker {

	private SummaryStat stat = new SummaryStat();

	public void addDecision(int decision) {
		// TODO Hmm now I need to add in the decision factor that Hildebrandt and Branke tried out.
	}

	@Override
	public SummaryStat getResults() {
		return stat;
	}

	@Override
	public void clear() {
		stat.clear();
	}

}
