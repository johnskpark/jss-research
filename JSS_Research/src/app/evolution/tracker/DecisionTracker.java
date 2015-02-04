package app.evolution.tracker;

import jasima.core.statistics.SummaryStat;

import java.util.ArrayList;
import java.util.List;

import app.evolution.IJasimaTracker;

public class DecisionTracker implements IJasimaTracker {

	List<Integer> decisions = new ArrayList<Integer>();

	public void addDecision(int decision) {
		decisions.add(decision);
	}

	@Override
	public SummaryStat getResults() {
		return null; // TODO
	}

	@Override
	public void clear() {
		decisions.clear();
	}

}
