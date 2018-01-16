package app.evolution;

import java.util.Arrays;
import java.util.List;

import app.IJasimaWorkStationListener;
import app.TrackedRuleBase;
import app.tracker.JasimaExperimentTracker;
import ec.EvolutionState;
import ec.Individual;
import jasima.shopSim.core.Job;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.core.WorkStation;
import jasima.shopSim.core.WorkStation.WorkStationEvent;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public abstract class GPPriorityRuleBase extends TrackedRuleBase<Individual> implements
		IJasimaGPPriorityRule,
		IJasimaWorkStationListener {

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
			PriorityQueue<Job> q = notifier.queue;

			Job[] entryByPrio = new Job[q.size()];
			q.getAllElementsInOrder(entryByPrio);

			List<PrioRuleTarget> entryRankings = Arrays.asList(entryByPrio);

			jobSelected(entry, entryRankings, q);
		}
	}

	@Override
	public void jobSelected(PrioRuleTarget entry,
			List<PrioRuleTarget> entryRankings,
			PriorityQueue<?> q) {
		if (hasTracker()) {
			getTracker().addStartTime(entry.getShop().simTime());
			getTracker().addSelectedEntry(this, entry);
			getTracker().addEntryRankings(this, entryRankings);

			clear();
		}
	}

}
