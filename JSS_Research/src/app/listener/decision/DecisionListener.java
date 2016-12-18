package app.listener.decision;

import app.JasimaWorkStationListener;
import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;

public class DecisionListener extends JasimaWorkStationListener {

	@Override
	protected void operationCompleted(WorkStation m, PrioRuleTarget justCompleted) {
		PR rule = m.queue.getSequencingRule();

		// TODO get the results of the rule from here.
	}

	@Override
	protected void operationStarted(WorkStation m,
			PrioRuleTarget justStarted,
			int oldSetupState,
			int newSetupState,
			double setupTime) {
		PR rule = m.queue.getSequencingRule();

		int index = m.index();
		double simTime = m.shop().simTime();

		// TODO get the results of the rule from here.

		// So what do I actually want here?
	}

	@Override
	protected void init(WorkStation m) {
		// TODO
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

}
