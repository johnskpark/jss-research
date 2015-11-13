package app.evolution.priorityRules;

import jasima.shopSim.core.PrioRuleTarget;
import app.evolution.AbsGPPriorityRule;
import app.evolution.JasimaGPConfig;
import app.evolution.JasimaGPData;
import ec.EvolutionState;
import ec.gp.GPIndividual;

public class BasicPriorityRule extends AbsGPPriorityRule {

	private static final long serialVersionUID = 5215861545303707980L;

	private EvolutionState state; // TODO this is common between all classes.
	private GPIndividual individual;
	private int threadnum; // TODO this is common between all classes.

	private JasimaGPData data; // TODO this is common between all classes.

	@Override
	public void setConfiguration(JasimaGPConfig config) {
		super.setConfiguration(config);

		individual = config.getIndividuals()[0];
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		data.setPrioRuleTarget(entry);

		individual.trees[0].child.eval(state, threadnum, data, null, individual, null);

		return data.getPriority();
	}

}
