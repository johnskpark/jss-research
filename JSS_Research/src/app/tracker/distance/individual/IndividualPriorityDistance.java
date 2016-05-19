package app.tracker.distance.individual;

import java.util.List;

import app.simConfig.SimConfig;
import app.tracker.JasimaDecision;
import app.tracker.JasimaExperiment;
import app.tracker.JasimaPriorityStat;
import app.tracker.distance.DistanceMeasure;
import ec.EvolutionState;
import ec.Individual;
import jasima.shopSim.core.PrioRuleTarget;

// TODO update this.
public class IndividualPriorityDistance implements DistanceMeasure {

	@Override
	public double[][] getDistances(final EvolutionState state,
			final JasimaExperiment experiment,
			final SimConfig simConfig,
			final Individual[] inds) {
		double[][] distances = new double[inds.length][inds.length];

		List<JasimaDecision> decisions = experiment.getDecisions();

		for (JasimaDecision decision : decisions) {
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
	protected double[] getNormPriorities(final JasimaDecision decision, final Individual[] inds) {
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
			double minPriority = Double.POSITIVE_INFINITY;
			double maxPriority = Double.NEGATIVE_INFINITY;

			for (int j = 0; j < entries.length; j++) {
				minPriority = Math.min(minPriority, priorities[j]);
				maxPriority = Math.max(maxPriority, priorities[j]);
			}

			double diff = maxPriority - minPriority;

			for (int j = 0; j < entries.length; j++) {
				PrioRuleTarget entry = entries[j];

				double normalisedPriority = (diff != 0.0) ? (priorities[j] - minPriority) / diff : 0.0;

				if (selectedEntry.equals(entry)) {
					selectedEntryPriority = normalisedPriority;
				}
			}

			normPriorities[i] = selectedEntryPriority;
		}

		return normPriorities;
	}

}
