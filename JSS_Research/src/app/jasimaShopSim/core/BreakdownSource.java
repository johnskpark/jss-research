package app.jasimaShopSim.core;

import jasima.core.simulation.Event;
import jasima.shopSim.core.DowntimeSource;
import jasima.shopSim.core.IndividualMachine;
import jasima.shopSim.core.JobShop;
import jasima.shopSim.core.WorkStation;

public class BreakdownSource extends DowntimeSource {

	public BreakdownSource(IndividualMachine machine) {
		super(machine);
	}

	@Override
	protected void onActivate() {
		if (isSourceActive()) {
			final IndividualMachine machine = getMachine();
			JobShop shop = machine.workStation.shop();

			// schedule next downtime
			double nextFailure = calcDeactivateTime(shop);
			shop.schedule(new Event(nextFailure, WorkStation.TAKE_DOWN_PRIO) {
				@Override
				public void handle() {
					assert machine.workStation.currMachine == null;
					machine.workStation.currMachine = machine;
					machine.takeDown(BreakdownSource.this);
					machine.workStation.currMachine = null;
				}

				@Override
				public boolean isAppEvent() {
					return false;
				}
			});
		}
	}

	@Override
	protected void onDeactivate() {
		final IndividualMachine machine = getMachine();
		JobShop shop = machine.workStation.shop();

		double whenReactivated = calcActivateTime(shop);
		machine.procFinished = whenReactivated;

		// schedule reactivation
		shop.schedule(new Event(whenReactivated, WorkStation.ACTIVATE_PRIO) {
			@Override
			public void handle() {
				assert machine.workStation.currMachine == null;
				machine.workStation.currMachine = machine;
				machine.activate();
				machine.workStation.currMachine = null;
			}

			@Override
			public boolean isAppEvent() {
				return false;
			}
		});
	}


}
