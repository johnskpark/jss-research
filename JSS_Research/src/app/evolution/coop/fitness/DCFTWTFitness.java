package app.evolution.coop.fitness;

import java.util.List;
import java.util.Map;

import app.evolution.JasimaFitnessBase;
import app.evolution.IJasimaFitness;
import app.evolution.coop.JasimaCoopFitness;
import app.evolution.coop.JasimaCoopIndividual;
import app.stat.WeightedTardinessStat;
import ec.Individual;
import ec.gp.koza.KozaFitness;

public class DCFTWTFitness extends JasimaCoopFitness {

	@Override
	protected IJasimaFitness<JasimaCoopIndividual> generateFitness(Individual[] inds) {
		return new DCFFitness();
	}

	private class DCFFitness extends JasimaFitnessBase<JasimaCoopIndividual> {

		@Override
		public double getFitness(int expIndex, JasimaCoopIndividual ind, Map<String, Object> results) {
			List<Double> referenceStat = getProblem().getReferenceStat();

			if (!ind.isEvaluated()) {
				KozaFitness fitness = (KozaFitness) ind.getFitness();

				double prevFitness = ((KozaFitness) ind.getFitness()).standardizedFitness();

				// TODO


			}

			return WeightedTardinessStat.getNormTotalWeightedTardiness(results, referenceStat.get(expIndex));
		}
	}

}
