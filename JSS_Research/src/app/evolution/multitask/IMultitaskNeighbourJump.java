package app.evolution.multitask;

import ec.EvolutionState;

public interface IMultitaskNeighbourJump {

	public void init(final EvolutionState state);

	public void preprocessing(final EvolutionState state, final int threadnum);

	public boolean jumpToNeighbour(final EvolutionState state,
			final int subpopulation,
			final int currentTask,
			final int neighbourTask,
			final JasimaMultitaskIndividual ind,
			final int threadnum);

	public void addIndividualToTask(final EvolutionState state,
			final int subpopulation,
			final int task,
			final JasimaMultitaskIndividual ind,
			final int threadnum);

	public void clear();

}
