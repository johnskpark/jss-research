package app.evolution.priorityRules;

import java.util.List;

import app.evolution.AbsGPPriorityRule;
import app.evolution.JasimaGPConfig;
import ec.Individual;
import ec.gp.GPIndividual;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

public class MachineSpecificPriorityRule extends AbsGPPriorityRule {

	private static final long serialVersionUID = -1896929320056457916L;

	private Individual[] individuals;

	@Override
	public void setConfiguration(JasimaGPConfig config) {
		super.setConfiguration(config);

		individuals = config.getIndividuals();
	}

	@Override
	public Individual[] getIndividuals() {
		return individuals;
	}

	@Override
	public void init() {
		super.init();

		int numMachines = getOwner().shop().machines.length;

		if (individuals.length != numMachines) {
			throw new RuntimeException("The number of individuals (" + individuals.length + ") need to match the number of machines (" + numMachines + ")");
		}
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q ) {
		super.beforeCalc(q);

		// TODO fill this out later.
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		int machineIndex = entry.getCurrMachine().index();

		data.setPrioRuleTarget(entry);

		GPIndividual ind = (GPIndividual) individuals[machineIndex];
		ind.trees[0].child.eval(state, threadnum, data, null, ind, null);

		return data.getPriority();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || this.getClass() != o.getClass()) {
			return false;
		}

		MachineSpecificPriorityRule other = (MachineSpecificPriorityRule) o;

		if (this.individuals.length != other.individuals.length) {
			return false;
		}

		for (int i = 0; i < this.individuals.length; i++) {
			if (!this.individuals[i].equals(other.individuals[i])) {
				return false;
			}
		}
		return true;
	}

	@Override
	public List<PrioRuleTarget> getEntryRankings() {
		// TODO
		return null;
	}

	@Override
	public void clear() {
		// TODO fill this later.
	}

}
