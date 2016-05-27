package app.evolution;

import java.util.List;

import app.IMultiRule;
import ec.Individual;
import jasima.shopSim.core.PrioRuleTarget;

public interface IJasimaGPPriorityRule extends IMultiRule<Individual> {

	public void setConfiguration(JasimaGPConfig config);

	public List<PrioRuleTarget> getEntryRankings();

}
