package app.shopSimModels;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.distribution.ExponentialDistribution;

import jasima.core.random.continuous.DblDistribution;
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

	public DynamicMachineBreakdownShopExperiment() {
		super();
		addShopListener(new BasicJobStatCollector()); // TODO replace with a machine statistics listener.
	}

	@Override
	public void init() {
		super.init();

		// TODO need to stop the machine breakdowns after a certain point? Maybe not.
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

		double repairTime = 0.0; // TODO
		double breakdownTime = 0.0; // TODO

		src.setTimeBetweenFailures(new DblDistribution(
				new ExponentialDistribution(breakdownTime)));
		src.setTimeToRepair(new DblDistribution(
				new ExponentialDistribution(repairTime)));

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
