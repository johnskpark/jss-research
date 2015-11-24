package app.evolution;

import jasima.shopSim.core.PR;
import app.tracker.JasimaEvolveExperimentTracker;
import ec.EvolutionState;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public abstract class AbsGPPriorityRule extends PR implements IJasimaGPPriorityRule {

	private static final long serialVersionUID = 5132364772745774943L;

	protected EvolutionState state;
	protected int threadnum;

	protected JasimaGPData data;
	protected JasimaEvolveExperimentTracker tracker;

	@Override
	public void setConfiguration(JasimaGPConfig config) {
		state = config.getState();
		threadnum = config.getThreadnum();

		data = config.getData();

		if (config.hasTracker()) {
			tracker = config.getTracker();
			tracker.setPriorityRule(this);
		}
	}

}
