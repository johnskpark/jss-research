package app.evolution.ensemble.grouped;

import jasima.core.util.Pair;
import app.evolution.IJasimaGPProblem;
import app.evolution.IJasimaTracker;
import ec.gp.GPIndividual;

public interface IJasimaGroupedTracker extends IJasimaTracker {

	public void setProblem(IJasimaGPProblem problem);

	public Pair<GPIndividual, Double>[] getResults();

	public void clear();

}
