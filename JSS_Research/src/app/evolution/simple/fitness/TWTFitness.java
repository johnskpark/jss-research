package app.evolution.simple.fitness;

import java.util.Map;

import app.evolution.JasimaFitnessBase;
import app.evolution.JasimaGPIndividual;
import app.simConfig.SimConfig;
import app.stat.WeightedTardinessStat;

public class TWTFitness extends JasimaFitnessBase<JasimaGPIndividual> {

	@Override
	public double getFitness(int expIndex, SimConfig config, JasimaGPIndividual ind, Map<String, Object> results) {
		return WeightedTardinessStat.getTotalWeightedTardiness(results);
	}

}
