package app.evolution;

import jasima.core.util.Pair;
import ec.gp.GPIndividual;

public interface IJasimaTracker {

	public Pair<GPIndividual, Double>[] getResults();

	public void clear();

}
