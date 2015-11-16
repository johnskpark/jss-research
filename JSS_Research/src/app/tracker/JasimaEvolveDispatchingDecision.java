package app.tracker;

import jasima.shopSim.core.PrioRuleTarget;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import app.evolution.IJasimaGPPriorityRule;
import app.simConfig.AbsSimConfig;
import ec.Individual;
import ec.util.Pair;

// So the tracker's going to generate one of these for each dispatching decision.
public class JasimaEvolveDispatchingDecision {

	private double startTime;

	private List<PrioRuleTarget> entries = new ArrayList<PrioRuleTarget>();
	private List<PrioRuleTarget> entryRankings = new ArrayList<PrioRuleTarget>();

	private PrioRuleTarget startedEntry;

	// This needs to be

	private Map<Individual, PriorityQueue<Pair<PrioRuleTarget, Double>>> decisionMakers = new HashMap<Individual, PriorityQueue<Pair<PrioRuleTarget, Double>>>();
	private PriorityComparator priorityComparator = new PriorityComparator();

	public JasimaEvolveDispatchingDecision() {
		// Keep the constructor empty for now.
	}

	public void addPriority(Individual ind, PrioRuleTarget entry, double priority) {
		if (!decisionMakers.containsKey(ind)) {
			decisionMakers.put(ind, new PriorityQueue<Pair<PrioRuleTarget, Double>>(priorityComparator));
		}
	}

	// FIXME come up with a better name.
	public void postProcessing(IJasimaGPPriorityRule priorityRule, AbsSimConfig simConfig) {
		entryRankings = priorityRule.getJobRankings();

		// TODO Right, something here.
	}

	// Getters

	public double getStartTime() {
		return startTime;
	}

	public List<PrioRuleTarget> getEntries() {
		return entries;
	}

	public List<PrioRuleTarget> getEntryRankings() {
		return entryRankings;
	}

	public PrioRuleTarget getStartedEntry() {
		return startedEntry;
	}

	public Map<Individual, PriorityQueue<Pair<PrioRuleTarget, Double>>> getDecisionMakers() {
		return decisionMakers;
	}

	// Setters

	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}

	public void setEntries(List<PrioRuleTarget> entries) {
		this.entries = entries;
	}

	public void setStartedEntry(PrioRuleTarget startedEntry) {
		this.startedEntry = startedEntry;
	}

	public void setDecisionMakers(Map<Individual, PriorityQueue<Pair<PrioRuleTarget, Double>>> decisionMakers) {
		this.decisionMakers = decisionMakers;
	}

	private class PriorityComparator implements Comparator<Pair<PrioRuleTarget, Double>> {

		public int compare(Pair<PrioRuleTarget, Double> entry1,
				Pair<PrioRuleTarget, Double> entry2) {
			if (entry1.i2 > entry2.i2) {
				return -1;
			} else if (entry1.i2 < entry2.i2) {
				return 1;
			} else {
				return 0;
			}
		}
	}

}
