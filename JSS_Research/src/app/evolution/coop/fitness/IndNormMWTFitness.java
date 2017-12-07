package app.evolution.coop.fitness;

import java.util.List;
import java.util.Map;

import app.evolution.JasimaFitnessBase;
import app.evolution.coop.JasimaCoopIndividual;
import app.simConfig.SimConfig;
import app.stat.WeightedTardinessStat;

public class IndNormMWTFitness extends JasimaFitnessBase<JasimaCoopIndividual> {

	@Override
	public double getFitness(int expIndex,
			SimConfig config,
			JasimaCoopIndividual reproducible,
			Map<String, Object> results) {
		List<Double> referenceStat = getProblem().getReferenceStat();

		return WeightedTardinessStat.getNormMeanWeightedTardiness(results, referenceStat.get(expIndex));
	}

}
