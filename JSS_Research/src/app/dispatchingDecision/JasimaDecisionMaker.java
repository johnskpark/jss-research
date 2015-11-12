package app.dispatchingDecision;

import jasima.shopSim.core.PrioRuleTarget;

import java.util.HashMap;
import java.util.Map;

public class JasimaDecisionMaker {

	private Map<JasimaDispatchingDecision, PrioRuleTarget> dispatchingDecisions = new HashMap<JasimaDispatchingDecision, PrioRuleTarget>();

	public JasimaDecisionMaker() {
		// Keep the constructor empty for now.
	}

	public Map<JasimaDispatchingDecision, PrioRuleTarget> getDispatchingDecisions() {
		return dispatchingDecisions;
	}

}
