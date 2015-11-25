package app.evolution.multilevel_new.niching;

import java.util.List;

import app.simConfig.AbsSimConfig;
import app.tracker.JasimaEvolveDecision;
import app.tracker.JasimaEvolveExperiment;
import ec.EvolutionState;
import ec.multilevel_new.MLSSubpopulation;

public class ANHGPPriorityNiching extends MultilevelANHGPNiching {

	@Override
	public double[][] getDistances(EvolutionState state, JasimaEvolveExperiment experiment, AbsSimConfig simConfig,
			MLSSubpopulation group) {
		List<JasimaEvolveDecision> decisions = experiment.getDecisions();
		double[][] distances = new double[group.individuals.length][group.individuals.length];
		
		for (JasimaEvolveDecision decision : decisions) {
			// TODO 
		}
		
		// TODO Auto-generated method stub
		return distances;
	}

}
