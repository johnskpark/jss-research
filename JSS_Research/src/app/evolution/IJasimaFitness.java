package app.evolution;

import java.util.Map;

import app.Clearable;
import app.simConfig.SimConfig;
import ec.EvolutionState;

public interface IJasimaFitness<T extends JasimaReproducible> extends Clearable {

	public void setProblem(JasimaGPProblem problem);

	public void accumulateFitness(final int index,
			final SimConfig config,
			final T reproducible,
			final Map<String, Object> results);

	public double getFitness(final int index,
			final SimConfig config,
			final T reproducible,
			final Map<String, Object> results);

	public void setFitness(final EvolutionState state,
			final SimConfig config,
			final T reproducible);

	public double getFinalFitness(final EvolutionState state,
			final SimConfig config,
			final T reproducible);

}
