package app.evaluation;

import java.util.Arrays;
import java.util.List;

import app.Clearable;
import app.TrackedRuleBase;
import app.node.INode;
import app.node.NodeData;
import app.tracker.JasimaExperimentTracker;
import jasima.core.util.observer.NotifierListener;
import jasima.shopSim.core.Job;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.core.WorkStation;
import jasima.shopSim.core.WorkStation.WorkStationEvent;

public abstract class EvalPriorityRuleBase extends TrackedRuleBase<INode>
		implements IJasimaEvalPriorityRule,
		NotifierListener<WorkStation, WorkStationEvent>,
		Clearable {

	private static final long serialVersionUID = -4755178527963577302L;

	public static final int SIZE_NOT_SET = -1;

	private long seed;
	private NodeData nodeData;

	private JasimaExperimentTracker<INode> tracker;

	public boolean hasTracker() {
		return tracker != null;
	}

	// Getters

	public long getSeed() {
		return seed;
	}

	public NodeData getNodeData() {
		return nodeData;
	}

	public JasimaExperimentTracker<INode> getTracker() {
		return tracker;
	}

	// Setters

	public void setSeed(long seed) {
		this.seed = seed;
	}

	public void setNodeData(NodeData data) {
		this.nodeData = data;
	}

	public void setTracker(JasimaExperimentTracker<INode> tracker) {
		this.tracker = tracker;
	}

	public abstract int getNumRules();

	public abstract int getRuleSize(int index);

	@Override
	public void update(WorkStation notifier, WorkStationEvent event) {
//		if (event == WorkStation.WS_JOB_SELECTED && hasTracker()) {
//			PrioRuleTarget entry = notifier.justStarted;
//			PriorityQueue<Job> q = notifier.queue;
//
//			Job[] entryByPrio = new Job[q.size()];
//			q.getAllElementsInOrder(entryByPrio);
//
//			List<PrioRuleTarget> entryRankings = Arrays.asList(entryByPrio);
//
//			jobSelected(entry, entryRankings, q);
//		}
	}

	@Override
	public void jobSelected(PrioRuleTarget entry,
			List<PrioRuleTarget> entryRankings,
			PriorityQueue<?> q) {
		if (hasTracker()) {
			getTracker().addStartTime(entry.getShop().simTime());
			getTracker().addSelectedEntry(this, entry);
			getTracker().addEntryRankings(this, entryRankings);
		}

		clear();
	}

}
