package app.evolution.coop.niching;

import jasima.core.util.Pair;
import jasima.shopSim.core.PrioRuleTarget;

import java.util.List;
import java.util.Map;

import app.simConfig.AbsSimConfig;
import app.tracker.JasimaEvolveDecision;
import app.tracker.JasimaEvolveExperiment;
import app.tracker.JasimaPriorityStat;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;

public class ANHGPPriorityNiching extends CoopANHGPNiching {

	@Override
	public double[][] getDistances(final EvolutionState state,
			final JasimaEvolveExperiment experiment,
			final AbsSimConfig simConfig,
			final Individual[] collaborators) {
		double[][] distances = new double[collaborators.length][collaborators.length];

		List<JasimaEvolveDecision> decisions = experiment.getDecisions();

		for (JasimaEvolveDecision decision : decisions) {
			// Get the normalised priorities assigned to the selected job by the individuals.
			double[] priorities = getNormPriorities(decision, collaborators);

			// Find the squared distances between the priorities.
			for (int i = 0; i < collaborators.length; i++) {
				for (int j = 0; j < collaborators.length; j++) {
					distances[i][j] += (priorities[i] - priorities[j]) * (priorities[i] - priorities[j]);
				}
			}
		}

		// Normalise the distances by taking the root mean of the distances over all decisions.
		for (int i = 0; i < collaborators.length; i++) {
			for (int j = 0; j < collaborators.length; j++) {
				distances[i][j] = Math.sqrt(distances[i][j] / decisions.size());
			}
		}

		return distances;
	}
	protected double[] getNormPriorities(final JasimaEvolveDecision decision, final Individual[] collaborators) {
		double[] priorities = new double[collaborators.length];

		List<PrioRuleTarget> entryRankingByGroup = decision.getEntryRankings();
		PrioRuleTarget selectedEntry = entryRankingByGroup.get(0);

		Map<GPIndividual, JasimaPriorityStat> indPriorityMap = decision.getDecisionMakers();

		for (int i = 0; i < collaborators.length; i++) {
			Pair<PrioRuleTarget, Double>[] entries = indPriorityMap.get(collaborators[i]).getEntries();

			// Get the priority assigned to the selected job by the individuals after normalisation.
			double selectedEntryPriority = -1;
			double sumPriorities = 0.0;

			// Get the maximum priority.
			double maxEntryPriority = Double.NEGATIVE_INFINITY;
			for (int j = 0; j < entries.length; j++) {
				maxEntryPriority = Math.max(maxEntryPriority, entries[j].b);
			}

			for (int j = 0; j < entries.length; j++) {
				PrioRuleTarget entry = entries[j].a;
				double normalisedPriority = Math.exp(entries[j].b - maxEntryPriority);

				if (selectedEntry.equals(entry)) {
					selectedEntryPriority = normalisedPriority;
				}

				sumPriorities += normalisedPriority;
			}

			selectedEntryPriority = selectedEntryPriority / sumPriorities;

			priorities[i] = selectedEntryPriority;
		}

		return priorities;
	}

}
