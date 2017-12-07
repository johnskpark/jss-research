package app.evolution.multilevel.fitness;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.evolution.multilevel.IJasimaMultilevelFitnessListener;
import app.evolution.multilevel.JasimaMultilevelGroupFitness;
import app.evolution.multilevel.JasimaMultilevelStatistics;
import app.simConfig.SimConfig;
import app.stat.WeightedTardinessStat;
import ec.EvolutionState;
import ec.multilevel.MLSSubpopulation;

public class GroupPrefSizeNormTWTFitness extends JasimaMultilevelGroupFitness {

	private static final int PREFERRED_SIZE = 3;

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
	public double getFitness(int expIndex, SimConfig config, MLSSubpopulation subpop, Map<String, Object> results) {
		double twt = WeightedTardinessStat.getTotalWeightedTardiness(results);

		for (IJasimaMultilevelFitnessListener listener : listeners) {
			listener.addFitness(JasimaMultilevelStatistics.ENSEMBLE_FITNESS, expIndex, twt);
		}

		return twt;
	}

	@Override
	public double getFinalFitness(final EvolutionState state, final SimConfig config, final MLSSubpopulation subpop) {
		double avgFitness = super.getFinalFitness(state, config, subpop);

		int size = subpop.individuals.length;
		double sizeFactor = (size - PREFERRED_SIZE) * (size - PREFERRED_SIZE) / (size * PREFERRED_SIZE);
		double sizeAdjustedFitness = avgFitness * (1 + sizeFactor);

		return sizeAdjustedFitness;
	}

}
