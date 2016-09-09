package app.priorityRules;

import app.listener.breakdown.BreakdownListener;
import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.prioRules.basic.SPT;

public class MBSPT extends MBPR {

	private static final long serialVersionUID = 3376768221888266245L;

	private PR spt = new SPT();

	public MBSPT(double threshold, BreakdownListener statCollector) {
		super(threshold, statCollector);
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		if (addRepairTime(entry)) {
			return spt.calcPrio(entry) + getMeanRepairTime(entry);
		} else {
			return spt.calcPrio(entry);
		}
	}

}
