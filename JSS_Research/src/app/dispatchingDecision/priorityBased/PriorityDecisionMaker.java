package app.dispatchingDecision.priorityBased;

import java.util.HashMap;
import java.util.Map;

import app.dispatchingDecision.JasimaDecisionMaker;
import app.dispatchingDecision.JasimaDispatchingDecision;

public class PriorityDecisionMaker extends JasimaDecisionMaker {

	private Map<JasimaDispatchingDecision, Double> priorityDecisions = new HashMap<JasimaDispatchingDecision, Double>();

	public PriorityDecisionMaker() {
		// Empty constructor (for now).
	}

	public Map<JasimaDispatchingDecision, Double> getPriorityDecisions() {
		return priorityDecisions;
	}

}
