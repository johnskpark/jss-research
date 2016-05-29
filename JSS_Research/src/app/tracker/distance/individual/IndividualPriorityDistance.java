package app.tracker.distance.individual;

import java.util.List;

import app.IMultiRule;
import app.simConfig.SimConfig;
import app.tracker.JasimaDecision;
import app.tracker.JasimaExperiment;
import app.tracker.JasimaPriorityStat;
import app.tracker.distance.DistanceMeasure;
import ec.EvolutionState;
import jasima.shopSim.core.PrioRuleTarget;

public class IndividualPriorityDistance<T> implements DistanceMeasure<T> {

	@Override
	public double[][] getDistances(final EvolutionState state,
			final JasimaExperiment<T> experiment,
			final SimConfig simConfig,
			final IMultiRule<T> solver,
			final List<T> ruleComponents) {
		int numComponents = ruleComponents.size();
		double[][] distances = new double[numComponents][numComponents];

		List<JasimaDecision<T>> decisions = experiment.getDecisions();

		for (JasimaDecision<T> decision : decisions) {
			// Get the normalised priorities assigned to the selected job by the individuals.
			double[] priorities = getNormPriorities(decision, solver, ruleComponents, numComponents);

			// Find the squared distances between the priorities.
			for (int i = 0; i < numComponents; i++) {
				for (int j = 0; j < numComponents; j++) {
					distances[i][j] += (priorities[i] - priorities[j]) * (priorities[i] - priorities[j]);
				}
			}
		}

		// Normalise the distances by taking the root mean of the distances over all decisions.
		for (int i = 0; i < numComponents; i++) {
			for (int j = 0; j < numComponents; j++) {
				distances[i][j] = Math.sqrt(distances[i][j] / decisions.size());
			}
		}

		return distances;
	}

	protected double[] getNormPriorities(final JasimaDecision<T> decision,
			final IMultiRule<T> solver,
			final List<T> ruleComponents,
			final int numComponents) {
		double[] normPriorities = new double[numComponents];

		List<PrioRuleTarget> entryRankingByGroup = decision.getEntryRankings();
		PrioRuleTarget selectedEntry = entryRankingByGroup.get(0);

		JasimaPriorityStat[] stats = decision.getStats(solver);

		for (int i = 0; i < numComponents; i++) {
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
