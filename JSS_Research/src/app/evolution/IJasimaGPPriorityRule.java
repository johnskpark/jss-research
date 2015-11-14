package app.evolution;

import jasima.shopSim.core.PrioRuleTarget;

import java.util.List;

public interface IJasimaGPPriorityRule {

	public void setConfiguration(JasimaGPConfig config);

	public List<PrioRuleTarget> getJobRankings();

}
