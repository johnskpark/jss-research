package app.jasimaShopSim.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.distribution.ExponentialDistribution;

import app.jasimaShopSim.core.BreakdownSource;
import app.jasimaShopSim.core.IndividualBreakdownMachine;
import app.jasimaShopSim.util.BreakdownStatCollector;
import jasima.core.random.continuous.DblDistribution;
import jasima.shopSim.core.IndividualMachine;
import jasima.shopSim.core.WorkStation;
import jasima.shopSim.models.dynamicShop.DynamicShopExperiment;
import jasima.shopSim.util.MachineStatCollector;

public class DynamicBreakdownShopExperiment extends DynamicShopExperiment {

	private static final long serialVersionUID = 4039817323228849037L;

	// Default parameters for machine breakdown.
	private double repairTimeFactor = 5.0;
	private double breakdownLevel = 0.025;
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
			BreakdownSource src = createBreakdownSource(shop.machines[i].currMachine);

			breakdownSrc.add(src);
		}
	}

	private void configureMachines() {
		WorkStation[] workStations = shop.getMachines();
		for (int n = 0; n < getNumMachines(); n++) {
			// Replace the IndividualMachine data in the WorkStation
			// with IndividualBreakdownMachine
			IndividualBreakdownMachine machine = new IndividualBreakdownMachine(workStations[n], n);
			workStations[n].machDat()[0] = machine;
		}
	}

	protected BreakdownSource createBreakdownSource(IndividualMachine machine) {
		BreakdownSource src = new BreakdownSource(machine);

		double meanProcTime = getProcTimes().getNumericalMean();

		// Repair time is dependent on the processing time, and the down time is
		// dependent on repair time and breakdown level.
		double meanRepairTime = repairTimeFactor * meanProcTime;
		double meanBreakdown = meanRepairTime / breakdownLevel - meanRepairTime;

		src.setName(machine.name);
		src.setTimeBetweenFailures(new DblDistribution(
				machineRand, new ExponentialDistribution(meanBreakdown)));
		src.setTimeToRepair(new DblDistribution(
				machineRand, new ExponentialDistribution(meanRepairTime)));

		return src;
	}

	public void setRepairTimeFactor(double repairTimeFactor) {
		this.repairTimeFactor = repairTimeFactor;
	}

	public double getRepairTimeFactor() {
		return repairTimeFactor;
	}

	public void setBreakdownLevel(double breakdownLevel) {
		this.breakdownLevel = breakdownLevel;
	}

	public double getBreakdownLevel() {
		return breakdownLevel;
	}

	public void setMachineRandom(Random machineRand) {
		this.machineRand = machineRand;
	}

	public Random getMachineRandom() {
		return machineRand;
	}

}
