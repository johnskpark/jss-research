package app.evolution;

import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;
import jasima.shopSim.core.WorkStation.WorkStationEvent;
import app.IWorkStationListener;
import app.tracker.JasimaEvolveExperimentTracker;
import ec.EvolutionState;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public abstract class AbsGPPriorityRule extends PR implements IJasimaGPPriorityRule, IWorkStationListener {

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

	@Override
	public void update(WorkStation notifier, WorkStationEvent event) {
		if (event == WorkStation.WS_JOB_SELECTED && tracker != null) {
			PrioRuleTarget entry = notifier.justStarted;

			tracker.addSelectedEntry(entry);
			tracker.addStartTime(entry.getShop().simTime());
			tracker.addEntryRankings(getEntryRankings());

			clear();
		}
	}

}
