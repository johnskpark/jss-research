package app.evolution.simple.fitness;

import java.util.List;
import java.util.Map;

import app.evolution.AbsJasimaFitness;
import app.evolution.JasimaGPIndividual;
import app.evolution.JasimaGPProblem;
import app.stat.WeightedTardinessStat;

public class NormTWTFitness extends AbsJasimaFitness<JasimaGPIndividual> {

	@Override
	protected double getFitness(int expIndex, JasimaGPIndividual ind, Map<String, Object> results) {
		JasimaGPProblem problem = getProblem();

		List<Double> referenceStat = problem.getReferenceStat();

		double twt = WeightedTardinessStat.getTotalWeightedTardiness(results);
		double normTWT = referenceStat.get(expIndex) / twt;

		return normTWT;
	}

}
