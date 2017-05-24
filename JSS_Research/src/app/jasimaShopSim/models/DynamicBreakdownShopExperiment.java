package app.jasimaShopSim.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import app.jasimaShopSim.core.BreakdownSource;
import app.jasimaShopSim.core.IndividualBreakdownMachine;
import app.jasimaShopSim.util.BreakdownStatCollector;
import jasima.core.random.continuous.DblStream;
import jasima.shopSim.core.IndividualMachine;
import jasima.shopSim.core.WorkStation;
import jasima.shopSim.models.dynamicShop.DynamicShopExperiment;
import jasima.shopSim.util.MachineStatCollector;

public class DynamicBreakdownShopExperiment extends DynamicShopExperiment {

	private static final long serialVersionUID = 4039817323228849037L;

	// Default parameters for machine breakdown.
	private DblStream repairTimeFactor;
	private DblStream breakdownLevel;
	private Random machineRand = new Random();

	protected List<BreakdownSource> breakdownSrc;

	public DynamicBreakdownShopExperiment() {
		super();
		addMachineListener(new MachineStatCollector());
		addMachineListener(new BreakdownStatCollector());
	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	protected void configureShop() {
		super.configureShop();

		configureMachines();

		breakdownSrc = new ArrayList<BreakdownSource>();

		for (int i = 0; i < getNumMachines(); i++) {
			BreakdownSource src = createBreakdownSource(shop.machines[i].machDat()[0]);

			breakdownSrc.add(src);
		}
	}

	private void configureMachines() {
		WorkStation[] workStations = shop.getMachines();
		for (int n = 0; n < getNumMachines(); n++) {
			// Replace the IndividualMachine data in the WorkStation with IndividualBreakdownMachine
			IndividualBreakdownMachine machine = new IndividualBreakdownMachine(workStations[n], n);
			workStations[n].machDat()[0] = machine;
		}
	}

	protected BreakdownSource createBreakdownSource(IndividualMachine machine) {
		if (repairTimeFactor == null || breakdownLevel == null) {
			throw new IllegalStateException("Repair Time and Time Between Failures Distributions are not initialised.");
		}
		BreakdownSource src = new BreakdownSource(machine);

		src.setName(machine.name);
		src.setTimeBetweenFailures(getTimeBetweenFailureDistribution());
		src.setTimeToRepair(getRepairTimeDistribution());

		machine.addDowntimeSource(src);

		return src;
	}

	public void setRepairTimeDistribution(DblStream repairTimeFactor) {
		this.repairTimeFactor = repairTimeFactor;
	}

	public DblStream getRepairTimeDistribution() {
		return repairTimeFactor;
	}

	public void setTimeBetweenFailureDistribution(DblStream breakdownLevel) {
		this.breakdownLevel = breakdownLevel;
	}

	public DblStream getTimeBetweenFailureDistribution() {
		return breakdownLevel;
	}

	public void setMachineRandom(Random machineRand) {
		this.machineRand = machineRand;
	}

	public Random getMachineRandom() {
		return machineRand;
	}

}
