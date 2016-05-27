package app.priorityRules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import app.tracker.DecisionEvent;
import app.tracker.JasimaExperimentTracker;
import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.core.WorkStation;

public class TrackedPR extends PR {

	private static final long serialVersionUID = -6359385279252431755L;

	private PR referenceRule;
	private int numJobThreshold;
	private int numSample;
	private long seed;

	private boolean firstRun = true;
	private List<DecisionEvent> recordedEvents;
	private Set<DecisionEvent> sampledEvents;
	private Random rand;

	private List<PR> priorityRules = new ArrayList<>();

	private JasimaExperimentTracker tracker;

	public TrackedPR(PR refRule, int jobThreshold, int sample, long s, JasimaExperimentTracker t) {
		super();

		referenceRule = refRule;
		numJobThreshold = jobThreshold;
		numSample = sample;
		seed = s;

		recordedEvents = new ArrayList<>();
		tracker = t;
	}

	public boolean isSampleRun() {
		return firstRun;
	}

	public void initSampleRun() {
		firstRun = true;
		recordedEvents = new ArrayList<>();
	}

	public void initTrackedRun() {
		firstRun = false;

		List<DecisionEvent> copy = new ArrayList<>(recordedEvents);
		sampledEvents = new HashSet<>();
		rand = new Random(seed);

		for (int i = 0; i < numSample; i++) {
			int index = rand.nextInt(copy.size());
			sampledEvents.add(copy.remove(index));
		}
	}

	public List<PR> getPriorityRules() {
		return priorityRules;
	}

	public void setPriorityRules(List<PR> priorityRules) {
		this.priorityRules = priorityRules;
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		super.beforeCalc(q);

		if (q.size() < numJobThreshold) {
			return;
		}

		if (firstRun) {
			// Record this particular decision situation.
			DecisionEvent event = getDecisionEvent(q.get(0));

			recordedEvents.add(event);
		} else if (sampledEvents.contains(getDecisionEvent(q.get(0)))) { // Determine whether this belongs to one of the sampled events.
			// Run it over the different rules.
			tracker.addDispatchingDecision(q);

			for (PR pr : priorityRules) {
				beforeCalcPR(pr, q);
			}
		}
	}

	protected void beforeCalcPR(PR pr, PriorityQueue<?> q) {
		pr.beforeCalc(q);

		for (int i = 0; i < q.size(); i++) {
			pr.calcPrio(q.get(i));
		}
	}

	private DecisionEvent getDecisionEvent(PrioRuleTarget entry) {
		WorkStation machine = entry.getCurrMachine();
		double simTime = entry.getShop().simTime();

		return new DecisionEvent(machine, simTime);
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		return referenceRule.calcPrio(entry);
	}

}
