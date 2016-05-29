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

public class IndividualDecisionDistance<T> implements DistanceMeasure<T> {

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
			// Get the ranks assigned to the individual voted job by the ensemble.
			double[] rankings = getEntryRankings(decision, solver, ruleComponents, numComponents);

			// Find the squared distances between the ranks.
			for (int i = 0; i < numComponents; i++) {
				for (int j = 0; j < numComponents; j++) {
					distances[i][j] += (rankings[i] - rankings[j]) * (rankings[i] - rankings[j]);
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

	protected double[] getEntryRankings(final JasimaDecision<T> decision,
			final IMultiRule<T> solver,
			final List<T> ruleComponents,
			final int numComponents) {
		double[] rankings = new double[numComponents];

		List<PrioRuleTarget> entryRankings = decision.getEntryRankings();
		JasimaPriorityStat[] stats = decision.getStats(solver);

		for (int i = 0; i < numComponents; i++) {
			JasimaPriorityStat stat = stats[i];

			rankings[i] = 1.0 * entryRankings.indexOf(stat.getBestEntry()) / entryRankings.size();
		}

		return rankings;
	}

}
