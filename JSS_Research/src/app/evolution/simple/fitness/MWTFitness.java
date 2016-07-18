package app.evolution.simple.fitness;

import java.util.Map;

import app.evolution.JasimaFitnessBase;
import app.evolution.JasimaGPIndividual;
import app.stat.WeightedTardinessStat;

public class MWTFitness extends JasimaFitnessBase<JasimaGPIndividual> {

	@Override
	public double getFitness(int expIndex, JasimaGPIndividual ind, Map<String, Object> results) {
		return WeightedTardinessStat.getMeanWeightedTardiness(results);
	}

}
