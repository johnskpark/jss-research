package app.evolution.priorityRules;

import java.util.List;

import app.evolution.AbsGPPriorityRule;
import app.evolution.JasimaGPConfig;
import ec.Individual;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

public class WeightedVoteRule extends AbsGPPriorityRule {

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
	public void beforeCalc(PriorityQueue<?> q) {
		super.beforeCalc(q);

		// TODO Auto-generated method stub
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		// TODO Auto-generated method stub

		return 0;
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub

		return false;
	}

	@Override
	public List<PrioRuleTarget> getEntryRankings() {
		// TODO Auto-generated method stub

		return null;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
	}

}
