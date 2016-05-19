package app.tracker;

import java.util.List;

public class JasimaDecisionMaker {

	private List<JasimaPriorityStat> priorityStats;

	public JasimaDecisionMaker() {
	}

	public void setPriorityStats(List<JasimaPriorityStat> priorityStats) {
		this.priorityStats = priorityStats;
	}

	public void addStat(JasimaPriorityStat stat) {
		priorityStats.add(stat);
	}

}
