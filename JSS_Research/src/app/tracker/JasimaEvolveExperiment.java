package app.tracker;

import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ec.Individual;
import ec.gp.GPIndividual;

// TODO trying to figure out how to do this efficiently.
public class JasimaEvolveExperiment {

	private GPIndividual[] inds;

	private List<JasimaEvolveDecisionMaker> decisionMakers;
	private Map<GPIndividual, JasimaEvolveDecisionMaker> decisionMakerMap;

	private List<JasimaEvolveDecision> experimentDecisions;

	private JasimaEvolveDecision currentDecision;
	private Map<GPIndividual, JasimaPriorityStat> currentDecisionsMap;

	public JasimaEvolveExperiment(GPIndividual[] inds) {
		this.inds = inds;

		decisionMakers = new ArrayList<JasimaEvolveDecisionMaker>(inds.length);
		decisionMakerMap = new HashMap<GPIndividual, JasimaEvolveDecisionMaker>(inds.length);

		for (GPIndividual ind : inds) {
			List<JasimaPriorityStat> priorityStats = new ArrayList<JasimaPriorityStat>();

			JasimaEvolveDecisionMaker decisionMaker = new JasimaEvolveDecisionMaker();
			decisionMaker.setPriorityStats(priorityStats);

			decisionMakers.add(decisionMaker);
			decisionMakerMap.put(ind, decisionMaker);
		}

		experimentDecisions = new ArrayList<JasimaEvolveDecision>();
	}

	public void addDispatchingDecision(PriorityQueue<?> q) {
		List<PrioRuleTarget> entries = new ArrayList<PrioRuleTarget>(q.size());
		for (int i = 0; i < q.size(); i++) {
			entries.add(q.get(i));
		}

		currentDecisionsMap = new HashMap<GPIndividual, JasimaPriorityStat>();

		for (GPIndividual ind : inds) {
			JasimaPriorityStat stat = new JasimaPriorityStat(q.size());

			currentDecisionsMap.put(ind, stat);
			decisionMakerMap.get(ind).addStat(stat);
		}

		currentDecision = new JasimaEvolveDecision(entries, currentDecisionsMap);
		experimentDecisions.add(currentDecision);
	}

	public void addPriority(int index, Individual ind, PrioRuleTarget entry, double priority) {
		currentDecisionsMap.get(ind).add(entry, priority);
	}

	public void addSelectedEntry(PrioRuleTarget entry) {
		currentDecision.setSelectedEntry(entry);
	}

	public void addStartTime(double time) {
		currentDecision.setStartTime(time);
	}

	public void addEntryRankings(List<PrioRuleTarget> rankings) {
		currentDecision.setEntryRankings(rankings);
	}

	public Map<GPIndividual, JasimaEvolveDecisionMaker> getDecisionMakers() {
		return decisionMakerMap;
	}

	public List<JasimaEvolveDecision> getDecisions() {
		return experimentDecisions;
	}

}
