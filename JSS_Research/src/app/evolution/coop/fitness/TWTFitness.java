package app.evolution.coop.fitness;

import java.util.Map;

import app.evolution.IJasimaFitness;
import app.evolution.JasimaFitnessBase;
import app.evolution.coop.JasimaCoopFitness;
import app.evolution.coop.JasimaCoopIndividual;
import app.simConfig.SimConfig;
import app.stat.WeightedTardinessStat;
import ec.Individual;

public class TWTFitness extends JasimaCoopFitness {

	@Override
	protected IJasimaFitness<JasimaCoopIndividual> generateFitness(final Individual[] inds) {
		return new IndividualTWTFitness();
	}

	private class IndividualTWTFitness extends JasimaFitnessBase<JasimaCoopIndividual> {

		@Override
		public double getFitness(int index,
				SimConfig config,
				JasimaCoopIndividual ind,
				Map<String, Object> results) {
			return WeightedTardinessStat.getTotalWeightedTardiness(results);
		}
	}

}
