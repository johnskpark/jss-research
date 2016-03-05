package app.evolution.multilevel.fitness;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.evolution.JasimaGPProblem;
import app.evolution.multilevel.IJasimaMultilevelFitnessListener;
import app.evolution.multilevel.JasimaMultilevelIndividual;
import app.evolution.multilevel.JasimaMultilevelIndividualFitness;
import app.evolution.multilevel.JasimaMultilevelStatistics;
import app.stat.WeightedTardinessStat;

/**
 * Fitness calculator for an individual in JasimaMultilevelProblem.
 *
 * Calculates the mean total weighted tardiness (TWT) value to use as the fitness
 * of the individual when the individual is applied to Jasima simulation as a
 * priority-based dispatching rule.
 *
 * @author parkjohn
 *
 */
public class IndividualNormTWTFitness extends JasimaMultilevelIndividualFitness {

	// FIXME not yet implemented.

	private List<IJasimaMultilevelFitnessListener> listeners = new ArrayList<IJasimaMultilevelFitnessListener>();

	@Override
	public void addListener(IJasimaMultilevelFitnessListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	@Override
	public void clearListeners() {
		listeners.clear();
	}

	@Override
	protected double getFitness(int expIndex, JasimaMultilevelIndividual ind, Map<String, Object> results) {
		JasimaGPProblem problem = getProblem();

		List<Double> referenceStat = problem.getReferenceStat();

		double twt = WeightedTardinessStat.getTotalWeightedTardiness(results);
		double normTWT = referenceStat.get(expIndex) / twt;

		for (IJasimaMultilevelFitnessListener listener : listeners) {
			listener.addFitness(JasimaMultilevelStatistics.INDIVIDUAL_FITNESS, expIndex, normTWT);
		}

		return normTWT;
	}

}
