package app.evolution;

import app.Clearable;
import app.tracker.JasimaExperimentTracker;
import ec.EvolutionState;
import ec.Individual;
import jasima.core.util.observer.NotifierListener;
import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;
import jasima.shopSim.core.WorkStation.WorkStationEvent;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public abstract class GPPriorityRuleBase extends PR implements
		IJasimaGPPriorityRule,
		NotifierListener<WorkStation, WorkStationEvent>,
		Clearable {

	private static final long serialVersionUID = 5132364772745774943L;

	protected EvolutionState state;
	protected int threadnum;

	protected JasimaGPData data;
	protected JasimaExperimentTracker<Individual> tracker;

	@Override
	public void setConfiguration(JasimaGPConfig config) {
		state = config.getState();
		threadnum = config.getThreadnum();

		data = config.getData();

		if (config.hasTracker()) {
			tracker = config.getTracker();
			tracker.addRule(this);
		}
	}

	protected boolean hasTracker() {
		return tracker != null;
	}

	protected JasimaExperimentTracker<Individual> getTracker() {
		return tracker;
	}

	@Override
	public void update(WorkStation notifier, WorkStationEvent event) {
		if (event == WorkStation.WS_JOB_SELECTED && hasTracker()) {
			PrioRuleTarget entry = notifier.justStarted;

			tracker.addStartTime(entry.getShop().simTime());
			tracker.addSelectedEntry(this, entry);
			tracker.addEntryRankings(this, getEntryRankings());

			clear();
		}
	}

}
