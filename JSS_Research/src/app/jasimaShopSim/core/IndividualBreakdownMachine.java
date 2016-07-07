package app.jasimaShopSim.core;

import jasima.shopSim.core.DowntimeSource;
import jasima.shopSim.core.IndividualMachine;
import jasima.shopSim.core.WorkStation;

public class IndividualBreakdownMachine extends IndividualMachine {

	public IndividualBreakdownMachine(WorkStation workStation, int idx) {
		super(workStation, idx);
	}

	@Override
	public void activate() {
		// TODO
	}

	@Override
	public void takeDown(final DowntimeSource downReason) {
		// TODO This needs to be modified.
	}

}
