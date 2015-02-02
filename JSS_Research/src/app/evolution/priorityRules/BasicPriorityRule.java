package app.evolution.priorityRules;

import jasima.shopSim.core.PrioRuleTarget;
import app.evolution.AbsPriorityRule;
import app.evolution.JasimaGPConfiguration;
import app.evolution.JasimaGPData;
import ec.EvolutionState;
import ec.gp.GPIndividual;

public class BasicPriorityRule extends AbsPriorityRule {

	private static final long serialVersionUID = 5215861545303707980L;

	private EvolutionState state;
	private GPIndividual individual;
	private int threadnum;

	private JasimaGPData data;

	@Override
	public void setConfiguration(JasimaGPConfiguration config) {
		state = config.getState();
		individual = config.getIndividuals()[0];
		data = config.getData();
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		data.setPrioRuleTarget(entry);

		individual.trees[0].child.eval(state, threadnum, data, null, individual, null);

		return data.getPriority();
	}

}
