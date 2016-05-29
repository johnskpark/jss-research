package app.evolution.multilevel.niching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.evolution.AbsGPPriorityRule;
import app.evolution.multilevel.IJasimaMultilevelFitnessListener;
import app.evolution.multilevel.IJasimaMultilevelNiching;
import app.evolution.multilevel.JasimaMultilevelStatistics;
import app.simConfig.SimConfig;
import app.tracker.JasimaExperiment;
import app.tracker.JasimaExperimentTracker;
import app.tracker.distance.DistanceMeasure;
import ec.EvolutionState;
import ec.Individual;
import ec.multilevel.MLSSubpopulation;
import ec.util.ParamClassLoadException;
import ec.util.Parameter;

// This is here to calculate the diversity measure that could be applied to the individuals,
// but is actually not applied to the individuals.
public class MultilevelNoNiching implements IJasimaMultilevelNiching {

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
			final AbsGPPriorityRule solver) {
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

		// Do not adjust the fitnesses of the individuals.
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
