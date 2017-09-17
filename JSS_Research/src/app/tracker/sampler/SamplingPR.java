package app.tracker.sampler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import app.TrackedRuleBase;
import app.node.INode;
import app.simConfig.SimConfig;
import app.tracker.DecisionEvent;
import app.tracker.JasimaExperimentTracker;
import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.core.WorkStation;

public class SamplingPR extends PR {

	private static final long serialVersionUID = -6359385279252431755L;

//	private static final int NUM_DP_ROUNDED = 4;

	// Default parameters here.
	private PR referenceRule;
	private int numJobThreshold = 10;
	private int numSample = 50;

	private long seed;

	private boolean firstRun = true;
	private List<List<DecisionEvent>> recordedEvents;
	private List<List<DecisionEvent>> sampledEvents;
	private Random rand;

	private List<DecisionEvent> currentRecording;
	private List<DecisionEvent> currentSample;

	private List<TrackedRuleBase<INode>> priorityRules = new ArrayList<>();

	private JasimaExperimentTracker<?> tracker;

	public SamplingPR(PR refRule, long s, JasimaExperimentTracker<?> t) {
		super();

		referenceRule = refRule;
		seed = s;

		recordedEvents = new ArrayList<>();
		sampledEvents = new ArrayList<>();
		tracker = t;
	}

	public boolean isSampleRun() {
		return firstRun;
	}

	public void initRecordingRun(SimConfig config, int configIndex) {
		firstRun = true;
		currentRecording = new ArrayList<>();
		recordedEvents.add(configIndex, currentRecording);
	}

	public void initTrackedRun(SimConfig config, int configIndex) {
		firstRun = false;
		currentSample = new ArrayList<>();
		sampledEvents.add(configIndex, currentSample);
		rand = new Random(seed);

		List<DecisionEvent> copy = new ArrayList<>(recordedEvents.get(configIndex));

		for (int i = 0; i < numSample && !copy.isEmpty(); i++) {
			int index = rand.nextInt(copy.size());

			DecisionEvent event = copy.remove(index);

			currentSample.add(event);
		}
	}

	public List<TrackedRuleBase<INode>> getPriorityRules() {
		return priorityRules;
	}

	public void setPriorityRules(List<TrackedRuleBase<INode>> priorityRules) {
		this.priorityRules = priorityRules;
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		super.beforeCalc(q);
		referenceRule.beforeCalc(q);

		if (isDecisionSampled(q)) {
			sampleDecision(q);
		}
	}

	protected boolean isDecisionSampled(PriorityQueue<?> q) {
		return q.size() >= numJobThreshold;
	}

	protected void sampleDecision(PriorityQueue<?> q) {
		DecisionEvent event = getDecisionEvent(q.get(0));

		if (isSampleRun()) {
			// Record this particular decision situation.
			currentRecording.add(event);
		} else {
			 // Determine whether this belongs to one of the sampled events.
			// Damn this thing is messy as fuck, how the fuck did I write this?
			if (currentSample.contains(event)) {
				// Run it over the different rules.
				tracker.addDispatchingDecision(q);

				for (TrackedRuleBase<INode> pr : priorityRules) {
					prPriorityCalculation(pr, q);
				}
			}
		}
	}

	protected void prPriorityCalculation(TrackedRuleBase<INode> pr, PriorityQueue<?> q) {
		pr.beforeCalc(q);

		for (int i = 0; i < q.size(); i++) {
			pr.calcPrio(q.get(i));
		}

		pr.jobSelected(pr.getEntryRankings().get(0), q);
	}

	protected DecisionEvent getDecisionEvent(PrioRuleTarget entry) {
		WorkStation machine = entry.getCurrMachine();

		double simTime = entry.getShop().simTime();

		return new DecisionEvent(machine, simTime);
	}

	protected List<DecisionEvent> getCurrentRecording() {
		return currentRecording;
	}

	protected List<DecisionEvent> getCurrentSample() {
		return currentSample;
	}

	protected JasimaExperimentTracker<?> getTracker() {
		return tracker;
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		double prio = referenceRule.calcPrio(entry);

		return prio;
	}

}
