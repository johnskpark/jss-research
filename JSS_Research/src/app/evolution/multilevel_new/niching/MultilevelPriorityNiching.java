package app.evolution.multilevel_new.niching;

import java.util.List;
import java.util.Map;

import app.evolution.multilevel_new.IJasimaMultilevelNiching;
import app.simConfig.AbsSimConfig;
import app.tracker.JasimaEvolveDecision;
import app.tracker.JasimaEvolveExperiment;
import app.tracker.JasimaEvolveExperimentTracker;
import app.tracker.JasimaPriorityStat;
import ec.EvolutionState;
import ec.gp.GPIndividual;
import ec.gp.koza.KozaFitness;
import ec.multilevel_new.MLSSubpopulation;
import jasima.core.util.Pair;
import jasima.shopSim.core.PrioRuleTarget;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class MultilevelPriorityNiching implements IJasimaMultilevelNiching {

	@Override
	public void adjustFitness(final EvolutionState state,
			final JasimaEvolveExperimentTracker tracker,
			final MLSSubpopulation group) {
		List<JasimaEvolveExperiment> experiments = tracker.getResults();
		AbsSimConfig simConfig = tracker.getSimConfig();

		double[] adjustment = new double[group.individuals.length];

		for (int i = 0; i < simConfig.getNumConfigs(); i++) {
			List<JasimaEvolveDecision> decisions = experiments.get(i).getDecisions();

			for (JasimaEvolveDecision decision : decisions) {
				// Get the normalised priorities assigned to the selected job by the individuals.
				double[] priorities = getNormPrioritiesOfSelectedEntry(decision, group);

				// Find the root mean squared distance between the individuals from the normalised priorities.
				double[] rmsd = getRootMeanSquaredDistances(priorities);

				for (int j = 0; j < group.individuals.length; j++) {
					adjustment[j] += rmsd[j] / decisions.size();
				}
			}
		}

		// Adjust the fitnesses of the individuals according to the niching algorithm.
		for (int i = 0; i < group.individuals.length; i++) {
			adjustment[i] = adjustment[i] / simConfig.getNumConfigs();

			KozaFitness fitness = (KozaFitness) group.individuals[i].fitness;
			double standardisedFitness = fitness.standardizedFitness();
			double adjustedFitness = standardisedFitness * (1 + adjustment[i]);

			fitness.setStandardizedFitness(state, adjustedFitness);
		}
	}

	// Get the normalised priorities assigned to the selected entry.
	private double[] getNormPrioritiesOfSelectedEntry(final JasimaEvolveDecision decision, final MLSSubpopulation group) {
		List<PrioRuleTarget> entryRankingByGroup = decision.getEntryRankings();
		PrioRuleTarget selectedEntry = entryRankingByGroup.get(0);

		Map<GPIndividual, JasimaPriorityStat> indPriorityMap = decision.getDecisionMakers();

		double[] indPriorities = new double[group.individuals.length];

		for (int i = 0; i < group.individuals.length; i++) {
			Pair<PrioRuleTarget, Double>[] entries = indPriorityMap.get(group.individuals[i]).getEntries();

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

			indPriorities[i] = selectedEntryPriority;
		}

		return indPriorities;
	}

	// Calculate the root mean squared distances between the priorities.
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

}
