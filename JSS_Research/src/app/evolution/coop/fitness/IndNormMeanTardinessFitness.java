package app.evolution.coop.fitness;

import java.util.List;
import java.util.Map;

import app.evolution.JasimaFitnessBase;
import app.evolution.coop.JasimaCoopIndividual;
import app.stat.TardinessStat;

public class IndNormMeanTardinessFitness extends JasimaFitnessBase<JasimaCoopIndividual> {

	@Override
	public double getFitness(int expIndex, JasimaCoopIndividual reproducible, Map<String, Object> results) {
		List<Double> referenceStat = getProblem().getReferenceStat();

		return TardinessStat.getNormMeanTardiness(results, referenceStat.get(expIndex));
	}

}
