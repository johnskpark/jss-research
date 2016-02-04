package app.evolution.multilevel.niching;

import java.util.HashMap;
import java.util.Map;

import ec.Individual;

// Auxiliary class to store the adjustment made to an individual in the population.
public class MultilevelNichingHistory {

	private Map<Individual, Double> adjustmentMap = new HashMap<Individual, Double>();

	public boolean hasBeenAdjusted(Individual ind) {
		return adjustmentMap.containsKey(ind);
	}

	public boolean isLowerAdjust(Individual ind, double adjust) {
		if (!hasBeenAdjusted(ind)) {
			return true;
		}
		return adjustmentMap.get(ind) > adjust;
	}

	public void addAdjustment(Individual ind, double adjust) {
		if (!hasBeenAdjusted(ind) || adjustmentMap.get(ind) > adjust) {
			adjustmentMap.put(ind, adjust);
		}
	}

	public double getAdjustment(Individual ind) {
		return adjustmentMap.get(ind);
	}

	public void clear() {
		adjustmentMap.clear();
	}

}
