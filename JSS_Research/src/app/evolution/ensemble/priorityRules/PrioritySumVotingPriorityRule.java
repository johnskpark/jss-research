package app.evolution.ensemble.priorityRules;

import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import app.evolution.JasimaGPConfig;

public class PrioritySumVotingPriorityRule extends AbsGPEnsemblePriorityRule {

	private static final long serialVersionUID = 3564821499012799918L;

	@Override
	public void setConfiguration(JasimaGPConfig config) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		trackerValue.clear();

		// TODO
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		// TODO Auto-generated method stub
		return 0;
	}

}
