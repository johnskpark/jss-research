package app.evolution;

import java.util.List;

import app.ITrackedRule;
import ec.Individual;
import jasima.shopSim.core.PrioRuleTarget;

public interface IJasimaGPPriorityRule extends ITrackedRule<Individual> {

	public void setConfiguration(JasimaGPConfig config);

	public List<PrioRuleTarget> getEntryRankings();

}
