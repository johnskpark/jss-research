package app.evolution;

import jasima.core.statistics.SummaryStat;

public interface IJasimaTracker {

	public SummaryStat getResults();

	public void clear();

}
