package app.priorityRules;

import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.prioRules.basic.ATC;

/**
 * Vepsalainen and Morton's definition.
 * @author John
 *
 */
public class WATCPR extends ATC {

	private static final long serialVersionUID = -5200383919674123645L;

	public WATCPR() {
//		super(1.0);
		super(3.0);
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		super.beforeCalc(q);
	}


	public double calcPrio(PrioRuleTarget entry) {
		return super.calcPrio(entry);
	}

}
