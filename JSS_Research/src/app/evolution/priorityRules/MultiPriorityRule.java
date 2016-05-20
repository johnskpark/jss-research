package app.evolution.priorityRules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.evolution.AbsGPPriorityRule;
import app.evolution.JasimaGPConfig;
import app.tracker.JasimaExperimentTracker;
import ec.Individual;
import ec.gp.GPIndividual;
import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.prioRules.basic.ATC;

public abstract class MultiPriorityRule extends AbsGPPriorityRule {

	private static final long serialVersionUID = -4909211481701584132L;

	public PR tieBreakerPR = new ATC(3.0); // Placeholder.

	private GPIndividual[] gpInds;

	private Map<PrioRuleTarget, Score> jobScores = new HashMap<>();
	private List<Score> jobRankings = new ArrayList<>();

	public MultiPriorityRule() {
		super();
		setTieBreaker(tieBreakerPR);
	}

	@Override
	public void setConfiguration(JasimaGPConfig config) {
		super.setConfiguration(config);

		Individual[] inds = config.getIndividuals();

		gpInds = new GPIndividual[inds.length];
		for (int i = 0; i < inds.length; i++) {
			gpInds[i] = (GPIndividual) inds[i];
		}
	}

	@Override
	public Individual[] getIndividuals() {
		return gpInds;
	}

	// Set the GP individual.
	protected void setGPIndividuals(GPIndividual[] gpInds) {
		this.gpInds = gpInds;
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		super.beforeCalc(q);

		clear();
		initSituation(q);

		for (int i = 0; i < gpInds.length; i++) {
			double[] scores = calculateScore(gpInds[i], q);

			for (int j = 0; j < q.size(); j++) {
				jobScores.get(q.get(j)).addScore(scores[j]);
			}

			if (hasTracker()) {
				JasimaExperimentTracker tracker = getTracker();

				for (int j = 0; j < q.size(); j++) {
					tracker.addPriority(i, gpInds[i], q.get(j), scores[j]);
				}
			}
		}
	}

	// Initialise the job scores and rankings.
	protected void initSituation(PriorityQueue<?> q) {
		for (int i = 0; i < q.size(); i++) {
			PrioRuleTarget entry = q.get(i);
			Score score = new Score(entry);

			jobScores.put(entry, score);
			jobRankings.add(score);
		}
	}

	// Calculate the score using the priority queue.
	protected abstract double[] calculateScore(GPIndividual gpInd, PriorityQueue<?> q);

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		return jobScores.get(entry).getScore();
	}

	@Override
	public String getName() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName() + "[ " + gpInds[0].genotypeToString());

		for (int i = 1; i < gpInds.length; i++) {
			builder.append("," + gpInds[i].genotypeToString());
		}

		builder.append(" ]");
		return builder.toString();
	}

	@Override
	public List<PrioRuleTarget> getEntryRankings() {
		Collections.sort(jobRankings);

		List<PrioRuleTarget> entries = new ArrayList<>();
		for (Score s : jobRankings) {
			entries.add(s.getEntry());
		}

		return entries;
	}

	@Override
	public void clear() {
		jobScores.clear();
		jobRankings.clear();
	}

	@Override
	public boolean equals(Object o) {
		// TODO
		return false;
	}

	private class Score implements Comparable<Score> {
		private PrioRuleTarget entry;
		private double score;

		public Score(PrioRuleTarget e) {
			entry = e;
			score = 0.0;
		}

		public void addScore(double s) {
			score += s;
		}

		public PrioRuleTarget getEntry() {
			return entry;
		}

		public double getScore() {
			return score;
		}

		@Override
		public int compareTo(Score other) {
			if (this.score < other.score) {
				return -1;
			} else if (this.score > other.score) {
				return 1;
			} else {
				double prio1 = getTieBreaker().calcPrio(this.getEntry());
				double prio2 = getTieBreaker().calcPrio(other.getEntry());

				if (prio1 > prio2) {
					return -1;
				} else if (prio1 < prio2) {
					return 1;
				} else {
					return 0;
				}
			}
		}
	}

}
