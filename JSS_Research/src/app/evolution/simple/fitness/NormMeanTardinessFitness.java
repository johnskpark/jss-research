package app.evolution.simple.fitness;

import java.util.List;
import java.util.Map;

import app.evolution.JasimaFitnessBase;
import app.evolution.JasimaGPIndividual;
import app.simConfig.SimConfig;
import app.stat.TardinessStat;

public class NormMeanTardinessFitness extends JasimaFitnessBase<JasimaGPIndividual> {

	@Override
	public double getFitness(int expIndex, SimConfig config, JasimaGPIndividual ind, Map<String, Object> results) {
		List<Double> referenceStat = getProblem().getReferenceStat();

		return TardinessStat.getNormMeanTardiness(results, referenceStat.get(expIndex));
	}

}
