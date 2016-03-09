package app.priorityRules;

import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.prioRules.basic.ATC;

/**
 * TODO javadoc.
 * @author John
 *
 */
public class ATCPRNew extends ATC {

	private static final long serialVersionUID = -5200383919674123645L;

	public ATCPRNew() {
		super(3.0);
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		super.beforeCalc(q);
	}


	public double calcPrio(PrioRuleTarget entry) {
		return calcPrio(entry);
	}

}
