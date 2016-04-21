package app.evolution;

import java.util.List;

import ec.Individual;
import jasima.shopSim.core.PrioRuleTarget;

public interface IJasimaGPPriorityRule {

	public void setConfiguration(JasimaGPConfig config);

	public Individual[] getIndividuals();

	public List<PrioRuleTarget> getEntryRankings();

}
