package app.evolution.multilevel_new.niching;

import java.util.ArrayList;
import java.util.List;

import app.evolution.multilevel_new.IJasimaMultilevelFitnessListener;
import app.evolution.multilevel_new.IJasimaMultilevelNiching;
import app.evolution.multilevel_new.JasimaMultilevelNichingStatistics;
import app.simConfig.AbsSimConfig;
import app.tracker.JasimaEvolveExperiment;
import app.tracker.JasimaEvolveExperimentTracker;
import app.tracker.distance.DistanceMeasure;
import ec.EvolutionState;
import ec.multilevel_new.MLSSubpopulation;
import ec.util.ParamClassLoadException;
import ec.util.Parameter;

// This is here to calculate the diversity measure that could be applied to the individuals,
// but is actually not applied to the individuals.
public class MultilevelNoNiching implements IJasimaMultilevelNiching {

	private static final long serialVersionUID = 6391897489389514486L;

	public static final String P_DISTANCE = "distance";

	public static final double LEARNING_RATE = 0.5;

	private DistanceMeasure measure;
	private MultilevelNichingHistory history;

	private List<IJasimaMultilevelFitnessListener> listeners = new ArrayList<IJasimaMultilevelFitnessListener>();

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		try {
			measure = (DistanceMeasure) state.parameters.getInstanceForParameterEq(base.push(P_DISTANCE), null, DistanceMeasure.class);
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
			final JasimaEvolveExperimentTracker tracker,
			final MLSSubpopulation group) {
		List<JasimaEvolveExperiment> experiments = tracker.getResults();
		AbsSimConfig simConfig = tracker.getSimConfig();

		double[] adjustment = new double[group.individuals.length];

		// Calculate the adjustment from the individual's density.
		for (int i = 0; i < simConfig.getNumConfigs(); i++) {
			// Calculate the distances between the individuals.
			double[][] distances = measure.getDistances(state, experiments.get(i), simConfig, group.individuals);

			// Calculate the density of the individuals.
			double[] simAdjust = getAdjustments(state, distances);

			for (int j = 0; j < group.individuals.length; j++) {
				adjustment[j] += simAdjust[j];
			}
			
			// Add in the distances to the statistics.
			for (IJasimaMultilevelFitnessListener listener : listeners) {
				listener.addDiversity(JasimaMultilevelNichingStatistics.INDIVIDUAL_FITNESS, i, group.individuals, simAdjust);
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
