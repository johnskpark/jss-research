package app.evolution.coop.fitness;

import java.util.Map;

import app.evolution.AbsJasimaFitness;
import app.evolution.IJasimaFitness;
import app.evolution.coop.JasimaCoopFitness;
import app.evolution.coop.JasimaCoopIndividual;
import app.stat.WeightedTardinessStat;
import ec.Individual;

public class TWTFitness extends JasimaCoopFitness {

	@Override
	protected IJasimaFitness<JasimaCoopIndividual> generateFitness(final Individual[] inds) {
		return new IndividualTWTFitness();
	}

	private class IndividualTWTFitness extends AbsJasimaFitness<JasimaCoopIndividual> {

		@Override
		public double getFitness(int index, JasimaCoopIndividual ind, Map<String, Object> results) {
			return WeightedTardinessStat.getTotalWeightedTardiness(results);
		}
	}

}
