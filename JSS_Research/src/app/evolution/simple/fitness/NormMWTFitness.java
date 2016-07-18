package app.evolution.simple.fitness;

import java.util.List;
import java.util.Map;

import app.evolution.JasimaFitnessBase;
import app.evolution.JasimaGPIndividual;
import app.stat.WeightedTardinessStat;

public class NormMWTFitness extends JasimaFitnessBase<JasimaGPIndividual> {

	@Override
	public double getFitness(int expIndex, JasimaGPIndividual ind, Map<String, Object> results) {
		List<Double> referenceStat = getProblem().getReferenceStat();

		return WeightedTardinessStat.getNormMeanWeightedTardiness(results, referenceStat.get(expIndex));
	}

}
