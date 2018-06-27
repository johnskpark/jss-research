package app.jasimaShopSim.core;

import jasima.core.random.continuous.DblStream;
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
	protected boolean initialiseDblStream(DblStream stream) {
		return stream != null;
	}

	@Override
	protected void onActivate() {
		if (isSourceActive()) {
			final IndividualMachine machine = getMachine();
			JobShop shop = machine.workStation.shop();

			// schedule next downtime
			double nextDeactivation = calcDeactivateTime(shop);

			shop.schedule(new Event(nextDeactivation, WorkStation.TAKE_DOWN_PRIO) {
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

		// schedule reactivation
		double nextActivation = calcActivateTime(shop);

		machine.procFinished = nextActivation;

		shop.schedule(new Event(nextActivation, WorkStation.ACTIVATE_PRIO) {
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
