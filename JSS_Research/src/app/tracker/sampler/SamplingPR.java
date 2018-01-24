package app.tracker.sampler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import app.Clearable;
import app.TrackedRuleBase;
import app.simConfig.SimConfig;
import app.tracker.DecisionEvent;
import app.tracker.JasimaExperimentTracker;
import jasima.core.util.Pair;
import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.core.WorkStation;

public class SamplingPR<T> extends PR implements Clearable {

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

	private List<TrackedRuleBase<T>> priorityRules = new ArrayList<>();

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

	public List<TrackedRuleBase<T>> getPriorityRules() {
		return priorityRules;
	}

	public void setPriorityRules(List<TrackedRuleBase<T>> priorityRules) {
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

				for (TrackedRuleBase<T> pr : priorityRules) {
					prPriorityCalculation(pr, q);
				}
			}
		}
	}

	protected void prPriorityCalculation(TrackedRuleBase<T> pr, PriorityQueue<?> q) {
		pr.beforeCalc(q);

		List<Pair<PrioRuleTarget, Double>> entryPrio = new ArrayList<>();

		for (int i = 0; i < q.size(); i++) {
			PrioRuleTarget entry = q.get(i);
			entryPrio.add(new Pair<PrioRuleTarget, Double>(entry, pr.calcPrio(entry)));
		}

		Comparator<Pair<PrioRuleTarget, Double>> comp = new Comparator<Pair<PrioRuleTarget, Double>>()
		{
			@Override
			public int compare(Pair<PrioRuleTarget, Double> p1, Pair<PrioRuleTarget, Double> p2) {
				if (p1.b > p2.b) {
					return -1;
				} else if (p1.b < p2.b) {
					return 1;
				} else {
					return 0;
				}
			}
		};

		Collections.sort(entryPrio, comp);

		List<PrioRuleTarget> entryRankings = entryPrio.stream().map(x -> x.a).collect(Collectors.toList());

		pr.jobSelected(entryRankings.get(0), entryRankings, q);
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

	@Override
	public void clear() {
		// TODO write the code here.
	}

}
