package app.evolution.multitask.fitness;

import java.util.List;
import java.util.Map;

import app.evolution.JasimaGPIndividual;
import app.simConfig.SimConfig;
import app.stat.WeightedTardinessStat;

public class NormMWTBreakdownFitness extends MultitaskFitnessBase {

	@Override
	public double getFitness(final int index,
			final SimConfig config,
			final JasimaGPIndividual reproducible,
			final Map<String, Object> results) {
		List<Double> referenceStat = getProblem().getReferenceStat();

		return WeightedTardinessStat.getNormMeanWeightedTardiness(results, referenceStat.get(index));
	}

}
