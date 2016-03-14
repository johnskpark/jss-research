package app.evolution.multilevel.fitness;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.evolution.multilevel.IJasimaMultilevelFitnessListener;
import app.evolution.multilevel.JasimaMultilevelGroupFitness;
import app.evolution.multilevel.JasimaMultilevelStatistics;
import app.stat.WeightedTardinessStat;
import ec.EvolutionState;
import ec.multilevel.MLSSubpopulation;

/**
 * Fitness calculator for a group of individuals in JasimaMultilevelProblem.
 *
 * Calculates the mean total weighted tardiness (TWT) value of the group when
 * applied to Jasima simulations as an ensemble, and combines it with the
 * size of the group and the individuals' fitnesses to calculate a fitness
 * value for the group as a whole.
 *
 * @author parkjohn
 *
 */
public class GroupSizeAdjustedTWTFitness extends JasimaMultilevelGroupFitness {

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
	protected double getFitness(int expIndex, MLSSubpopulation subpop, Map<String, Object> results) {
		double twt = WeightedTardinessStat.getTotalWeightedTardiness(results);

		for (IJasimaMultilevelFitnessListener listener : listeners) {
			listener.addFitness(JasimaMultilevelStatistics.ENSEMBLE_FITNESS, expIndex, twt);
		}

		return twt;
	}

	@Override
	public double getFinalFitness(final EvolutionState state, final MLSSubpopulation subpop) {
		double avgFitness = super.getFinalFitness(state, subpop);

		int size = subpop.individuals.length;
		double sizeFactor = Math.sqrt((2.0 * size) / (2.0 + size));
		double sizeAdjustedFitness = avgFitness * sizeFactor;

		return sizeAdjustedFitness;
	}

}
