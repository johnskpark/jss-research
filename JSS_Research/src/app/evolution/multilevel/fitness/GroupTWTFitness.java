package app.evolution.multilevel.fitness;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.evolution.multilevel.IJasimaMultilevelFitnessListener;
import app.evolution.multilevel.JasimaMultilevelGroupFitness;
import app.evolution.multilevel.JasimaMultilevelStatistics;
import app.stat.WeightedTardinessStat;
import ec.multilevel_new.MLSSubpopulation;

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
public class GroupTWTFitness extends JasimaMultilevelGroupFitness {

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
			listener.addFitness(JasimaMultilevelStatistics.INDIVIDUAL_FITNESS, expIndex, twt);
		}		
		
		return twt;
	}

}
