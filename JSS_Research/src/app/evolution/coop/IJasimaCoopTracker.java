package app.evolution.coop;

import jasima.core.util.Pair;
import ec.gp.GPIndividual;
import app.evolution.IJasimaGPProblem;
import app.evolution.IJasimaTracker;

public interface IJasimaCoopTracker extends IJasimaTracker {

	public void setProblem(IJasimaGPProblem problem);

	public Pair<GPIndividual, Double>[] getResults();

	public void clear();

}
