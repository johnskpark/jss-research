package app.tracker.distance.individual;

import jasima.shopSim.core.PrioRuleTarget;

import java.util.List;

import app.simConfig.AbsSimConfig;
import app.tracker.JasimaEvolveDecision;
import app.tracker.JasimaEvolveExperiment;
import app.tracker.JasimaPriorityStat;
import app.tracker.distance.DistanceMeasure;
import ec.EvolutionState;
import ec.Individual;

public class IndividualPriorityDistance implements DistanceMeasure {

	@Override
	public double[][] getDistances(final EvolutionState state,
			final JasimaEvolveExperiment experiment,
			final AbsSimConfig simConfig,
			final Individual[] inds) {
		double[][] distances = new double[inds.length][inds.length];

		List<JasimaEvolveDecision> decisions = experiment.getDecisions();

		for (JasimaEvolveDecision decision : decisions) {
			// Get the normalised priorities assigned to the selected job by the individuals.
			double[] priorities = getNormPriorities(decision, inds);

			// Find the squared distances between the priorities.
			for (int i = 0; i < inds.length; i++) {
				for (int j = 0; j < inds.length; j++) {
					distances[i][j] += (priorities[i] - priorities[j]) * (priorities[i] - priorities[j]);
				}
			}
		}

		// Normalise the distances by taking the root mean of the distances over all decisions.
		for (int i = 0; i < inds.length; i++) {
			for (int j = 0; j < inds.length; j++) {
				distances[i][j] = Math.sqrt(distances[i][j] / decisions.size());
			}
		}

		return distances;
	}

	/**
	 * TODO javadoc.
	 */
	protected double[] getNormPriorities(final JasimaEvolveDecision decision, final Individual[] inds) {
		double[] normPriorities = new double[inds.length];

		List<PrioRuleTarget> entryRankingByGroup = decision.getEntryRankings();
		PrioRuleTarget selectedEntry = entryRankingByGroup.get(0);

		JasimaPriorityStat[] stats = decision.getStats();

		for (int i = 0; i < inds.length; i++) {
			JasimaPriorityStat stat = stats[i];
			PrioRuleTarget[] entries = stat.getEntries();
			double[] priorities = stat.getPriorities();

			// Get the priority assigned to the selected job by the individuals after normalisation.
			double selectedEntryPriority = -1;
			double sumPriorities = 0.0;

			// Get the maximum priority.
			double maxEntryPriority = Double.NEGATIVE_INFINITY;
			for (int j = 0; j < entries.length; j++) {
				maxEntryPriority = Math.max(maxEntryPriority, priorities[j]);
			}

			for (int j = 0; j < entries.length; j++) {
				PrioRuleTarget entry = entries[j];
				double normalisedPriority = Math.exp(priorities[j] - maxEntryPriority);

				if (selectedEntry.equals(entry)) {
					selectedEntryPriority = normalisedPriority;
				}

				sumPriorities += normalisedPriority;
			}

			selectedEntryPriority = selectedEntryPriority / sumPriorities;

			normPriorities[i] = selectedEntryPriority;
		}

		return normPriorities;
	}

}
