package app.evolution;

import java.util.Map;

import ec.EvolutionState;

public interface IJasimaFitness<T extends JasimaReproducible> {

	public void setProblem(JasimaGPProblem problem);
	
	public void accumulateFitness(final int index, final T reproducible, final Map<String, Object> results);

	public void setFitness(final EvolutionState state, final T reproducible);

	public void clear();

}
