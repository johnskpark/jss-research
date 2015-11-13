package app.evolution;

import jasima.shopSim.core.PR;
import ec.EvolutionState;

// TODO I need to get refactor this at some point.
public abstract class AbsGPPriorityRule extends PR implements IJasimaGPPriorityRule {

	private static final long serialVersionUID = 5132364772745774943L;

	protected EvolutionState state;
	protected int threadnum;

	protected JasimaGPData data;
	protected IJasimaNewTracker tracker;

	public void setConfiguration(JasimaGPConfig config) {
		state = config.getState();
		threadnum = config.getThreadnum();

		data = config.getData();
		tracker = config.getNewTracker();
	}

}
