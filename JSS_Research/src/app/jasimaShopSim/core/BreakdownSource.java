package app.jasimaShopSim.core;

import jasima.shopSim.core.DowntimeSource;
import jasima.shopSim.core.IndividualMachine;

public class BreakdownSource extends DowntimeSource {

	public BreakdownSource(IndividualMachine machine) {
		super(machine);
	}

}
