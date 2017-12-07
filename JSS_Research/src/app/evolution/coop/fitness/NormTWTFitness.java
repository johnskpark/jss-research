package app.evolution.coop.fitness;

import java.util.List;
import java.util.Map;

import app.evolution.IJasimaFitness;
import app.evolution.JasimaFitnessBase;
import app.evolution.coop.JasimaCoopFitness;
import app.evolution.coop.JasimaCoopIndividual;
import app.simConfig.SimConfig;
import app.stat.WeightedTardinessStat;
import ec.Individual;

public class NormTWTFitness extends JasimaCoopFitness {

	@Override
	protected IJasimaFitness<JasimaCoopIndividual> generateFitness(final Individual[] inds) {
		return new IndividualTWTFitness();
	}

	private class IndividualTWTFitness extends JasimaFitnessBase<JasimaCoopIndividual> {

		@Override
		public double getFitness(int expIndex,
				SimConfig config,
				JasimaCoopIndividual ind,
				Map<String, Object> results) {
			List<Double> referenceStat = getProblem().getReferenceStat();

			return WeightedTardinessStat.getNormTotalWeightedTardiness(results, referenceStat.get(expIndex));
		}
	}

}
