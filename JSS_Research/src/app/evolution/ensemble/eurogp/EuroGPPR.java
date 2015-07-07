package app.evolution.ensemble.eurogp;

import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.evolution.AbsGPPriorityRule;
import app.evolution.JasimaGPConfig;
import app.evolution.JasimaGPData;
import ec.EvolutionState;
import ec.gp.GPIndividual;

public class EuroGPPR extends AbsGPPriorityRule {

	private static final long serialVersionUID = -2159123752873667029L;

	public static final double ATC_K_VALUE = 3.0;

	private EvolutionState state;
	private GPIndividual[] gpInds;
	private int threadnum;

	private JasimaGPData data;
	private EuroGPTracker tracker;

	private Map<PrioRuleTarget, JobPriority> entries = new HashMap<PrioRuleTarget, JobPriority>();
	private PrioRuleTarget bestEnsembleEntry;

	public EuroGPPR() {
		super();
	}

	@Override
	public void setConfiguration(JasimaGPConfig config) {
		state = config.getState();
		gpInds = config.getIndividuals();
		threadnum = config.getThreadnum();
		data = config.getData();
		tracker = (EuroGPTracker) config.getTracker();
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		super.beforeCalc(q);

		entries.clear();
		bestEnsembleEntry = null;

		for (int i = 0; i < q.size(); i++) {
			entries.put(q.get(i), new JobPriority(q.get(i)));
		}

		double[][] priorities = new double[gpInds.length][q.size()];

		for (int i = 0; i < gpInds.length; i++) {
			double sumPriorities = 0.0;

			double bestPriority = Double.NEGATIVE_INFINITY;
			PrioRuleTarget bestEntry = null;

			// Find the job selected by the individual rule.
			for (int j = 0; j < q.size(); j++) {
				PrioRuleTarget entry = q.get(j);
				data.setPrioRuleTarget(entry);

				gpInds[i].trees[0].child.eval(state, threadnum, data, null, gpInds[i], null);

				// Normalisation via sigmoid function.
				double priority = 1.0 / (1 + Math.exp(-data.getPriority()));
				priorities[i][j] = priority;

				sumPriorities += priority;

				if (priority > bestPriority) {
					bestPriority = priority;
					bestEntry = entry;
				}
			}

			// TODO Temporary code.
			if (sumPriorities < 0.0001) {
				throw new RuntimeException("Issues with normalisation. Everything's turning up to be zero.");
			}

			for (int j = 0; j < q.size(); j++) {
				priorities[i][j] /= sumPriorities;
			}

			bestPriority /= sumPriorities;

			entries.get(bestEntry).addVote();
			entries.get(bestEntry).addPriority(bestPriority);
		}

		// TODO tracker data.

		List<JobPriority> jpList = new ArrayList<JobPriority>(entries.values());

		JobPriority bestjp = jpList.get(0);
		for (int i = 1; i < jpList.size(); i++) {
			JobPriority jp = jpList.get(i);
			bestjp = (bestjp.compareTo(jp) > 0) ? jp : bestjp;
		}

		bestEnsembleEntry = bestjp.getEntry();
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		return (entry.equals(bestEnsembleEntry)) ? 1 : 0;
	}

	// Stores the votes made on a particular job.
	private class JobPriority implements Comparable<JobPriority> {
		private int sumVotes = 0;
		private double sumPriorities = 0;

		private PrioRuleTarget entry;

		public JobPriority(PrioRuleTarget entry) {
			this.entry = entry;
		}

		public void addVote() {
			sumVotes++;
		}

		public void addPriority(double priority) {
			sumPriorities += priority;
		}

		public PrioRuleTarget getEntry() {
			return entry;
		}

		/************************************************/

		@Override
		public int compareTo(JobPriority other) {
			int diffVotes = compareVotes(other);
			if (diffVotes != 0) {
				return diffVotes;
			} else {
				return comparePriorities(other);
			}
		}

		private int compareVotes(JobPriority other) {
			return other.sumVotes - this.sumVotes;
		}

		private int comparePriorities(JobPriority other) {
			if (this.sumPriorities > other.sumPriorities) {
				return -1;
			} else if (this.sumPriorities < other.sumPriorities) {
				return 1;
			} else {
				return 0;
			}
		}
	}

}
