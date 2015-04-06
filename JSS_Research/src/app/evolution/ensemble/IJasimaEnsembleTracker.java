package app.evolution.ensemble;

import jasima.core.statistics.SummaryStat;
import jasima.core.util.Pair;
import app.evolution.IJasimaGPProblem;
import app.evolution.IJasimaTracker;
import ec.gp.GPIndividual;

public interface IJasimaEnsembleTracker extends IJasimaTracker {

	public void addTrackerValue(int jobFinished, EnsembleTrackerValue value);

	public void setProblem(IJasimaGPProblem problem);

	public Pair<GPIndividual, Double>[] getResults();

	public double getDiversityMeasure(SummaryStat stat);

	public void clear();

}
