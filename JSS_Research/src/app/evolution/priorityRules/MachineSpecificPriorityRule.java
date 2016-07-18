package app.evolution.priorityRules;

import java.util.Arrays;
import java.util.List;

import app.evolution.GPPriorityRuleBase;
import app.evolution.JasimaGPConfig;
import ec.Individual;
import ec.gp.GPIndividual;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

public class MachineSpecificPriorityRule extends GPPriorityRuleBase {

	private static final long serialVersionUID = -1896929320056457916L;

	private List<Individual> individuals;

	@Override
	public void setConfiguration(JasimaGPConfig config) {
		super.setConfiguration(config);

		individuals = Arrays.asList(config.getIndividuals());
	}

	@Override
	public List<Individual> getRuleComponents() {
		return individuals;
	}

	@Override
	public void init() {
		super.init();

		int numMachines = getOwner().shop().machines.length;

		if (individuals.size() != numMachines) {
			throw new RuntimeException("The number of individuals (" + individuals.size() + ") need to match the number of machines (" + numMachines + ")");
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

		GPIndividual ind = (GPIndividual) individuals.get(machineIndex);
		ind.trees[0].child.eval(state, threadnum, data, null, ind, null);

		return data.getPriority();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || this.getClass() != o.getClass()) {
			return false;
		}

		MachineSpecificPriorityRule other = (MachineSpecificPriorityRule) o;

		if (this.individuals.size() != other.individuals.size()) {
			return false;
		}

		for (int i = 0; i < this.individuals.size(); i++) {
			if (!this.individuals.get(i).equals(other.individuals.get(i))) {
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
