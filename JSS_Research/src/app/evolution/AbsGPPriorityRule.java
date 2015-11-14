package app.evolution;

import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;

import java.util.List;

import app.tracker.JasimaEvolveDecisionTracker;
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
	protected JasimaEvolveDecisionTracker tracker;


	@Override
	public void setConfiguration(JasimaGPConfig config) {
		state = config.getState();
		threadnum = config.getThreadnum();

		data = config.getData();
		tracker = config.getNewTracker();
	}


	// So what I've obtained from Mitch:
	// TODO have a strategy pattern here which calculates priorities and adds them to the tracker.

}
