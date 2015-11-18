package app.evolution.multilevel_new.niching;

import jasima.core.util.Pair;
import jasima.shopSim.core.PrioRuleTarget;

import java.util.List;
import java.util.Map;

import app.evolution.multilevel_new.IJasimaMultilevelNiching;
import app.tracker.JasimaEvolveDecisionTracker;
import app.tracker.JasimaEvolveDispatchingDecision;
import app.tracker.JasimaPriorityStat;
import ec.EvolutionState;
import ec.Individual;
import ec.multilevel_new.MLSSubpopulation;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class MultilevelPriorityNiching implements IJasimaMultilevelNiching {

	@Override
	public void adjustFitness(final EvolutionState state,
			final JasimaEvolveDecisionTracker tracker,
			final MLSSubpopulation group) {
		List<JasimaEvolveDispatchingDecision> decisions = tracker.getResults();

		double[] adjustment = new double[group.individuals.length];

		for (JasimaEvolveDispatchingDecision decision : decisions) {
			// Get the normalised priorities assigned to the selected job by the individuals.
			double[] priorities = getNormPrioritiesOfBestEntry(decision, group);

			// Find the root mean squared distance between the individuals from the normalised priorities.
			double[] rmsd = getRootMeanSquaredDistances(priorities);

			for (int i = 0; i < group.individuals.length; i++) {
				adjustment[i] += rmsd[i];
			}
		}

		// TODO modify the fitnesses of the individuals.
	}

	private double[] getNormPrioritiesOfBestEntry(final JasimaEvolveDispatchingDecision decision, final MLSSubpopulation group) {
		List<PrioRuleTarget> entryRankingByGroup = decision.getEntryRankings();
		PrioRuleTarget bestEntry = entryRankingByGroup.get(0);

		Map<Individual, JasimaPriorityStat> indPriorityMap = decision.getDecisionMakers();

		double[] indPriorities = new double[group.individuals.length];

		for (int i = 0; i < group.individuals.length; i++) {
			List<Pair<PrioRuleTarget, Double>> entries = indPriorityMap.get(group.individuals[i]).getEntries();

			// Get the priority assigned to the selected job by the individuals after normalisation.
			double bestEntryPriority = -1;
			double sumPriorities = 0.0;

			for (int j = 0; j < entries.size(); j++) {
				PrioRuleTarget entry = entries.get(j).a;
				double normalisedPriority = Math.exp(entries.get(j).b);

				if (bestEntry.equals(entry)) {
					bestEntryPriority = normalisedPriority;
				}

				sumPriorities += normalisedPriority;
			}

			bestEntryPriority = bestEntryPriority / sumPriorities;

			indPriorities[i] = bestEntryPriority;
		}

		return indPriorities;
	}

	private double[] getRootMeanSquaredDistances(double[] priorities) {
		double[] rmsd = new double[priorities.length];

		for (int i = 0; i < priorities.length; i++) {
			for (int j = i + 1; j < priorities.length; j++) {
				double sqDiff = (priorities[i] - priorities[j]) * (priorities[i] - priorities[j]);

				rmsd[i] += sqDiff;
				rmsd[j] += sqDiff;
			}

			rmsd[i] = Math.sqrt(rmsd[i] / priorities.length);
		}

		return rmsd;
	}

	@Override
	public void adjustFitness(final EvolutionState state,
			final JasimaEvolveDecisionTracker tracker) {
		// FIXME does nothing at the moment.
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

}
