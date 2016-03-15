package app.evolution.coop.fitness;

import java.util.List;
import java.util.Map;

import app.evolution.AbsJasimaFitness;
import app.evolution.coop.JasimaCoopIndividual;
import app.stat.WeightedTardinessStat;

public class IndNormTWTFitness extends AbsJasimaFitness<JasimaCoopIndividual> {

	@Override
	public double getFitness(int expIndex, JasimaCoopIndividual reproducible, Map<String, Object> results) {
		List<Double> referenceStat = getProblem().getReferenceStat();

		return WeightedTardinessStat.getNormTotalWeightedTardiness(results, referenceStat.get(expIndex));
	}

}
