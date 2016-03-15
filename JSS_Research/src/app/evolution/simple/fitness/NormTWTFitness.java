package app.evolution.simple.fitness;

import java.util.List;
import java.util.Map;

import app.evolution.AbsJasimaFitness;
import app.evolution.JasimaGPIndividual;
import app.stat.WeightedTardinessStat;

public class NormTWTFitness extends AbsJasimaFitness<JasimaGPIndividual> {

	@Override
	public double getFitness(int expIndex, JasimaGPIndividual ind, Map<String, Object> results) {
		List<Double> referenceStat = getProblem().getReferenceStat();

		return WeightedTardinessStat.getNormTotalWeightedTardiness(results, referenceStat.get(expIndex));
	}

}
