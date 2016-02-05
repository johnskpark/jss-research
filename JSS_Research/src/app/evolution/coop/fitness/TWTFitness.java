package app.evolution.coop.fitness;

import java.util.Map;

import app.evolution.AbsJasimaFitness;
import app.evolution.IJasimaFitness;
import app.evolution.coop.JasimaCoopFitness;
import app.evolution.coop.JasimaCoopIndividual;
import app.stat.WeightedTardinessStat;
import ec.EvolutionState;
import ec.Individual;

public class TWTFitness extends JasimaCoopFitness {

	@Override
	protected IJasimaFitness<JasimaCoopIndividual> getFitness(final Individual[] inds) {
		return new IndividualTWTFitness();
	}
	
	private class IndividualTWTFitness extends AbsJasimaFitness<JasimaCoopIndividual> {

		@Override
		protected double getFitness(int index, JasimaCoopIndividual ind, Map<String, Object> results) {
			return WeightedTardinessStat.getTotalWeightedTardiness(results);
		}
		
		@Override
		protected double getFinalFitness(final EvolutionState state, final JasimaCoopIndividual ind) {
			double avgFitness = super.getFinalFitness(state, ind);
			
//			if (shouldSetContext()) {
//				ind.fitness.setContext(getIndividuals());
//			}
			
			return avgFitness;
		}
	}

}
