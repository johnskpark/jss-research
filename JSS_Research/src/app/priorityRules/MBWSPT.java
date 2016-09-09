package app.priorityRules;

import app.listener.breakdown.BreakdownListener;
import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.prioRules.basic.SPT;
import jasima.shopSim.prioRules.weighted.WSPT;

public class MBWSPT extends MBPR {

	private static final long serialVersionUID = 3376768221888266245L;

	private PR wspt = new WSPT();
	private PR spt = new SPT();

	public MBWSPT(double threshold, BreakdownListener statCollector) {
		super(threshold, statCollector);
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		if (addRepairTime(entry)) {
			return entry.getWeight() / (spt.calcPrio(entry) + getMeanRepairTime(entry));
		} else {
			return wspt.calcPrio(entry);
		}
	}

}
