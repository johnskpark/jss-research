package app.evolution.simple.fitness;

import java.util.Map;

import app.evolution.AbsJasimaFitness;
import app.evolution.JasimaGPIndividual;
import app.stat.WeightedTardinessStat;

public class TWTFitness extends AbsJasimaFitness<JasimaGPIndividual> {

	@Override
	protected double getFitness(int expIndex, JasimaGPIndividual ind, Map<String, Object> results) {
		return WeightedTardinessStat.getTotalWeightedTardiness(results);
	}

}
