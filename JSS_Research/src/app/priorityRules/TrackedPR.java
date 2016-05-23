package app.priorityRules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import jasima.core.util.Pair;
import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

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

	public TrackedPR(PR refRule, int jobThreshold, int sample, long s) {
		super();

		referenceRule = refRule;
		numJobThreshold = jobThreshold;
		numSample = sample;
		seed = s;

		recordedEvents = new ArrayList<>();
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

		if (firstRun && q.size() >= numJobThreshold) {
			// Record this particular decision situation.
			DecisionEvent event = getDecisionEvent(q.get(0));

			recordedEvents.add(event);
		} else {
			// Determine whether this belongs to one of the sampled events.
			DecisionEvent event = getDecisionEvent(q.get(0));

			if (sampledEvents.contains(event)) {
				for (PR pr : priorityRules) {
					beforeCalcPR(pr, q);
				}
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
		int machineIndex = entry.getCurrMachine().index();
		double simTime = entry.getShop().simTime();

		return new DecisionEvent(machineIndex, simTime);
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		return referenceRule.calcPrio(entry);
	}

	private class DecisionEvent extends Pair<Integer, Double> {
		private static final long serialVersionUID = -3972902742837636133L;

		private static final double epsilon = 0.001;

		public DecisionEvent(int machineIndex, double simTime) {
			super(machineIndex, simTime);
		}

		public int getMachineIndex() {
			return a;
		}

		public double getSimTime() {
			return b;
		}

		@Override
		public boolean equals(Object o) {
			if (o == null || this.getClass() != o.getClass()) {
				return false;
			}

			DecisionEvent other = (DecisionEvent) o;
			if (this.getMachineIndex() != other.getMachineIndex()) {
				return false;
			} else if (this.getSimTime() - epsilon < other.getSimTime() ||
					this.getSimTime() + epsilon > other.getSimTime()) {
				return false;
			} else {
				return true;
			}
		}
	}

}
