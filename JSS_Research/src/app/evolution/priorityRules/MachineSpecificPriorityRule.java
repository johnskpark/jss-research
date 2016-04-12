package app.evolution.priorityRules;

import java.util.List;

import app.evolution.AbsGPPriorityRule;
import app.evolution.JasimaGPConfig;
import ec.gp.GPIndividual;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

public class MachineSpecificPriorityRule extends AbsGPPriorityRule {

	private static final long serialVersionUID = -1896929320056457916L;

	private GPIndividual[] individuals;

	@Override
	public void setConfiguration(JasimaGPConfig config) {
		super.setConfiguration(config);

		individuals = config.getIndividuals();
	}

	@Override
	public GPIndividual[] getIndividuals() {
		return individuals;
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q ) {
		super.beforeCalc(q);

		PrioRuleTarget entry = q.get(0);
		int numMachines = entry.getShop().machines.length;

		if (individuals.length != numMachines) {
			throw new RuntimeException("The number of individuals (" + individuals.length + ") need to match the number of machines (" + numMachines + ")");
		}
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		int machineIndex = entry.getCurrMachine().index();

		data.setPrioRuleTarget(entry);

		individuals[machineIndex].trees[0].child.eval(state, threadnum, data, null, individuals[machineIndex], null);

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
