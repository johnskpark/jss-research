package app.evolution;

import jasima.core.util.Pair;
import ec.gp.GPIndividual;

public interface IJasimaTracker {

	public void setProblem(JasimaGPProblem problem);

	public Pair<GPIndividual, Double>[] getResults();

	public void clear();

}
