package app.tracker;

import java.util.List;

public class JasimaEvolveDecisionMaker {

	private List<JasimaPriorityStat> priorityStats;

	public JasimaEvolveDecisionMaker() {
	}

	public void setPriorityStats(List<JasimaPriorityStat> priorityStats) {
		this.priorityStats = priorityStats;
	}

	public void addStat(JasimaPriorityStat stat) {
		priorityStats.add(stat);
	}

}
