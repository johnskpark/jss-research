package app.evolution;

import java.util.List;

import ec.gp.GPIndividual;
import jasima.shopSim.core.PrioRuleTarget;

public interface IJasimaGPPriorityRule {

	public void setConfiguration(JasimaGPConfig config);

	public GPIndividual[] getIndividuals();

	public List<PrioRuleTarget> getEntryRankings();

	public void clearRankings();

}
