package app.evolution.pickardt.fitness;

import java.util.Map;

import app.evolution.JasimaFitnessBase;
import app.evolution.pickardt.JasimaVectorIndividual;
import app.simConfig.SimConfig;
import app.stat.WeightedTardinessStat;

public class TWTFitness extends JasimaFitnessBase<JasimaVectorIndividual> {

	@Override
	public double getFitness(int expIndex, SimConfig config, JasimaVectorIndividual ind, Map<String, Object> results) {
		return WeightedTardinessStat.getTotalWeightedTardiness(results);
	}

}
