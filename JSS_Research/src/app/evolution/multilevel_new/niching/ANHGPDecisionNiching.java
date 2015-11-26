package app.evolution.multilevel_new.niching;

import java.util.List;

import app.simConfig.AbsSimConfig;
import app.tracker.JasimaEvolveDecision;
import app.tracker.JasimaEvolveExperiment;
import ec.EvolutionState;
import ec.multilevel_new.MLSSubpopulation;

public class ANHGPDecisionNiching extends MultilevelANHGPNiching {

	@Override
	public double[][] getDistances(EvolutionState state, JasimaEvolveExperiment experiment, AbsSimConfig simConfig,
			MLSSubpopulation group) {
		double[][] distances = new double[group.individuals.length][group.individuals.length];

		List<JasimaEvolveDecision> decisions = experiment.getDecisions();

		for (JasimaEvolveDecision decision : decisions) {
			// Get the ranks assigned to the individual voted job by the ensemble.
			double[] rankings = getEntryRankings(decision, group);

			// TODO
		}

		return distances;
	}

	protected double[] getEntryRankings(final JasimaEvolveDecision decision, final MLSSubpopulation group) {
		double[] rankings = new double[group.individuals.length];

		//List

		// TODO

		return rankings;
	}

}
