package app.tracker;

import jasima.shopSim.core.PrioRuleTarget;

import java.util.HashMap;
import java.util.Map;

// TODO I'm wondering if I even need this at the moment.
// TODO also, this shouldn't be called the decision maker, it should be called priority something.
public class JasimaPriorityStat {

	private Map<JasimaEvolveDispatchingDecision, PrioRuleTarget> dispatchingDecisions = new HashMap<JasimaEvolveDispatchingDecision, PrioRuleTarget>();

	// TODO other important informations.

	public JasimaPriorityStat() {
		// Keep the constructor empty for now.
	}

	public Map<JasimaEvolveDispatchingDecision, PrioRuleTarget> getDispatchingDecisions() {
		return dispatchingDecisions;
	}

}
