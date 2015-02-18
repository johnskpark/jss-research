package app.evolution.grouped;

import app.evolution.IJasimaGPProblem;
import jasima.core.util.Pair;
import ec.gp.GPIndividual;

public interface IJasimaGroupedTracker {

	public void setProblem(IJasimaGPProblem problem);

	public Pair<GPIndividual, Double>[] getResults();

	public void clear();

}
