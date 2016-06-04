package app.priorityRules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import app.AbsMultiRule;
import app.node.INode;
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
	private List<List<DecisionEvent>> recordedEvents;
	private List<Collection<DecisionEvent>> sampledEvents;
	private Random rand;

	private List<DecisionEvent> currentRecording;
	private Collection<DecisionEvent> currentSample;

	private List<AbsMultiRule<INode>> priorityRules = new ArrayList<>();

	private JasimaExperimentTracker<?> tracker;

	public TrackedPR(PR refRule, int jobThreshold, int sample, long s, JasimaExperimentTracker<?> t) {
		super();

		referenceRule = refRule;
		numJobThreshold = jobThreshold;
		numSample = sample;
		seed = s;

		recordedEvents = new ArrayList<>();
		sampledEvents = new ArrayList<>();
		tracker = t;
	}

	public boolean isSampleRun() {
		return firstRun;
	}

	public void initSampleRun(int configIndex) {
		firstRun = true;
		currentRecording = new ArrayList<>();
		recordedEvents.add(configIndex, currentRecording);
	}

	public void initTrackedRun(int configIndex) {
		firstRun = false;
		currentSample = new HashSet<>();
		sampledEvents.add(configIndex, currentSample);
		rand = new Random(seed);

		List<DecisionEvent> copy = new ArrayList<>(recordedEvents.get(configIndex));

		for (int i = 0; i < numSample && !copy.isEmpty(); i++) {
			int index = rand.nextInt(copy.size());

			DecisionEvent event = copy.remove(index);

			currentSample.add(event);
		}

		System.out.println(currentSample.size());
	}

	public List<AbsMultiRule<INode>> getPriorityRules() {
		return priorityRules;
	}

	public void setPriorityRules(List<AbsMultiRule<INode>> priorityRules) {
		this.priorityRules = priorityRules;
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		super.beforeCalc(q);

		if (q.size() < numJobThreshold) {
			return;
		}

		DecisionEvent event = getDecisionEvent(q.get(0));

		if (firstRun) {
			// Record this particular decision situation.
			currentRecording.add(event);
		} else {
			 // Determine whether this belongs to one of the sampled events.
			if (currentSample.contains(event)) {
				// Run it over the different rules.
				tracker.addDispatchingDecision(q);

				for (AbsMultiRule<INode> pr : priorityRules) {
					prPriorityCalculation(pr, q);
				}
			}
		}
	}

	protected void prPriorityCalculation(AbsMultiRule<INode> pr, PriorityQueue<?> q) {
		pr.beforeCalc(q);

		for (int i = 0; i < q.size(); i++) {
			pr.calcPrio(q.get(0));
		}

		pr.jobSelected(pr.getEntryRankings().get(0), q);
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
