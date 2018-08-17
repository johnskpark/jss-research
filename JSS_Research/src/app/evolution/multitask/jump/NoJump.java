package app.evolution.multitask.jump;

import app.evolution.multitask.IMultitaskNeighbourJump;
import app.evolution.multitask.JasimaMultitaskIndividual;
import ec.EvolutionState;
import ec.util.Parameter;

public class NoJump implements IMultitaskNeighbourJump {

	private static final long serialVersionUID = 1576932612315464017L;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		// No setup required.
	}

	@Override
	public void preprocessing(final EvolutionState state, final int threadnum) {
		// FIXME See if I need to override this.
	}

	@Override
	public boolean jumpToNeighbour(final EvolutionState state,
			final int subpopulation,
			final int currentTask,
			final int neighbourTask,
			final JasimaMultitaskIndividual ind,
			final int threadnum) {
		// Always returns false.
		return false;
	}

	@Override
	public void addIndividualToTask(final EvolutionState state,
			final int subpopulation,
			final int task,
			final JasimaMultitaskIndividual ind,
			final int threadnum) {
		// FIXME See if I need to override this.
	}

	@Override
	public void clear() {
		// FIXME See if I need to override this.
	}

}
