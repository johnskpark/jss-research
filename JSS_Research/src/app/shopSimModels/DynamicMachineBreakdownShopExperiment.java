package app.shopSimModels;

import java.util.ArrayList;
import java.util.List;

import jasima.shopSim.core.DowntimeSource;
import jasima.shopSim.core.IndividualMachine;
import jasima.shopSim.models.dynamicShop.DynamicShopExperiment;
import jasima.shopSim.util.BasicJobStatCollector;

public class DynamicMachineBreakdownShopExperiment extends DynamicShopExperiment {

	private static final long serialVersionUID = 4039817323228849037L;

	// Default parameters for machine breakdown.
	private double repairTimeFactor = 5.0;
	private double downtimeLevel = 0.025;

	protected List<IndividualMachine> indMachines;
	protected List<DowntimeSource> downtimeSrc;

	@Override
	public void init() {
		super.init();
		addShopListener(new BasicJobStatCollector()); // TODO replace with a machine statistics listener.
	}

	@Override
	protected void configureShop() {
		super.configureShop();

		indMachines = new ArrayList<IndividualMachine>();
		downtimeSrc = new ArrayList<DowntimeSource>();

		for (int i = 0; i < getNumMachines(); i++) {
			IndividualMachine machine = new IndividualMachine(shop.machines[i], i);
			DowntimeSource src = createDowntimeSource(machine);

			downtimeSrc.add(src);
		}
	}

	protected DowntimeSource createDowntimeSource(IndividualMachine machine) {
		DowntimeSource src = new DowntimeSource(machine);

		// TODO
		src.setTimeBetweenFailures(null);
		src.setTimeToRepair(null);

		return src;
	}

	public void setRepairTimeFactor(double repairTimeFactor) {
		this.repairTimeFactor = repairTimeFactor;
	}

	public double getRepairTimeFactor() {
		return repairTimeFactor;
	}

	public void setBreakdownLevel(double breakdownLevel) {
		this.downtimeLevel = breakdownLevel;
	}

	public double getBreakdownLevel() {
		return downtimeLevel;
	}

}
