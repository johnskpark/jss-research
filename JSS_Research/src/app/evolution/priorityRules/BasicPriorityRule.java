package app.evolution.priorityRules;

import jasima.shopSim.core.PrioRuleTarget;
import app.evolution.AbsGPPriorityRule;
import app.evolution.JasimaGPConfig;
import ec.gp.GPIndividual;

public class BasicPriorityRule extends AbsGPPriorityRule {

	private static final long serialVersionUID = 5215861545303707980L;

	private GPIndividual individual;

	private int indIndex;

	public BasicPriorityRule() {
		super();
		indIndex = 0;
	}

	public BasicPriorityRule(int indIndex) {
		super();
		this.indIndex = indIndex;
	}

	@Override
	public void setConfiguration(JasimaGPConfig config) {
		super.setConfiguration(config);

		individual = config.getIndividuals()[indIndex];
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		data.setPrioRuleTarget(entry);

		individual.trees[0].child.eval(state, threadnum, data, null, individual, null);

		double priority = data.getPriority();

		if (tracker != null) {
			
		}

		return priority;
	}

}
