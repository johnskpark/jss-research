package app.evolution.multilevel.niching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.evolution.GPPriorityRuleBase;
import app.evolution.multilevel.IJasimaMultilevelFitnessListener;
import app.evolution.multilevel.IJasimaMultilevelNiching;
import app.evolution.multilevel.JasimaMultilevelStatistics;
import app.simConfig.SimConfig;
import app.tracker.JasimaExperiment;
import app.tracker.JasimaExperimentTracker;
import app.tracker.distance.DistanceMeasure;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.koza.KozaFitness;
import ec.multilevel.MLSSubpopulation;
import ec.util.ParamClassLoadException;
import ec.util.Parameter;

public class MultilevelBasicNiching implements IJasimaMultilevelNiching {

	private static final long serialVersionUID = 6391897489389514486L;

	public static final String P_DISTANCE = "distance";

	public static final double LEARNING_RATE = 0.5;

	private DistanceMeasure<Individual> measure;
	private MultilevelNichingHistory history;

	private List<IJasimaMultilevelFitnessListener> listeners = new ArrayList<IJasimaMultilevelFitnessListener>();

	@SuppressWarnings("unchecked")
	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		try {
			measure = (DistanceMeasure<Individual>) state.parameters.getInstanceForParameterEq(base.push(P_DISTANCE), null, DistanceMeasure.class);
			history = new MultilevelNichingHistory();
		} catch (ParamClassLoadException ex) {
			state.output.fatal("The distance measure was not correctly initialised for MultilevelANHGPNiching.");
		}
	}

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
	public void adjustFitness(final EvolutionState state,
			final JasimaExperimentTracker<Individual> tracker,
			final MLSSubpopulation group,
			final GPPriorityRuleBase solver) {
		List<JasimaExperiment<Individual>> experiments = tracker.getResults();
		SimConfig simConfig = tracker.getSimConfig();

		List<Individual> individuals = Arrays.asList(group.individuals);

		double[] adjustment = new double[individuals.size()];

		// Calculate the adjustment from the individual's density.
		for (int i = 0; i < simConfig.getNumConfigs(); i++) {
			// Calculate the distances between the individuals.
			double[][] distances = measure.getDistances(state, experiments.get(i), simConfig, solver, individuals);

			// Calculate the density of the individuals.
			double[] simAdjust = getAdjustments(state, distances);

			for (int j = 0; j < individuals.size(); j++) {
				adjustment[j] += simAdjust[j];
			}

			// Add in the distances to the statistics.
			for (IJasimaMultilevelFitnessListener listener : listeners) {
				listener.addDiversity(JasimaMultilevelStatistics.INDIVIDUAL_FITNESS, i, individuals, simAdjust);
			}
		}

		// Adjust the fitnesses of the individuals according to the niching algorithm.
		for (int i = 0; i < group.individuals.length; i++) {
			adjustment[i] = adjustment[i] / simConfig.getNumConfigs();

			Individual ind = group.individuals[i];
			KozaFitness fitness = (KozaFitness) ind.fitness;

			// Look at the history and see if the adjustment is better than the current.
			if (!history.hasBeenAdjusted(ind)) {
				// Add in the adjustment.
				double standardised = fitness.standardizedFitness();
				double adjustedFitness = standardised * (1.0 + adjustment[i]);

				fitness.setStandardizedFitness(state, adjustedFitness);

				history.addAdjustment(ind, adjustment[i]);
			} else if (history.isLowerAdjust(ind, adjustment[i])) {
				// Revert the fitness back to the original and then readjust the fitness.
				double oldAdjust = history.getAdjustment(ind);
				double oldStandardised = fitness.standardizedFitness() / (1.0 + oldAdjust);
				double adjustedFitness = oldStandardised * (1.0 + adjustment[i]);

				fitness.setStandardizedFitness(state, adjustedFitness);

				history.addAdjustment(ind, adjustment[i]);
			}
		}
	}

	protected double[] getAdjustments(final EvolutionState state, double[][] distances) {
		double[] simAdjust = new double[distances.length];

		for (int i = 0; i < distances.length; i++) {
			for (int j = 0; j < distances[i].length; j++) {
				simAdjust[i] += distances[i][j];
			}

			simAdjust[i] = simAdjust[i] / distances[i].length;
		}

		return simAdjust;
	}

	public void clear() {
		history.clear();
	}

}
