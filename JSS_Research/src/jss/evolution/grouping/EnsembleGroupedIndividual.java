package jss.evolution.grouping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ec.EvolutionState;
import ec.gp.GPIndividual;
import ec.gp.koza.KozaFitness;
import ec.util.Parameter;
import jss.evolution.IGroupedIndividual;

public class EnsembleGroupedIndividual implements IGroupedIndividual {

	private static final long serialVersionUID = 5515931398086837728L;

	public static final String P_GROUP = "group";
	public static final String P_ITER = "iteration";

	private int groupSize = 3;
	private int numIterations = 3;

	private Map<GPIndividual, List<GPIndividual[]>> evalGroups = new HashMap<GPIndividual, List<GPIndividual[]>>();
	private GPIndividual[] bestGroup = null;
	private KozaFitness bestGroupFitness = new KozaFitness();

	private GPIndividual[] bestGroupOfGeneration = null;
	private KozaFitness bestGroupOfGenerationFitness = new KozaFitness();

	@Override
	public void setup(EvolutionState state, Parameter base) {
		// Set the group size used for the individuals.
		String groupSizeStr = state.parameters.getString(base.push(P_GROUP), null);
		if (groupSizeStr != null) {
			groupSize = Integer.parseInt(groupSizeStr);
		}

		// Set the number of iterations used for the individuals.
		String numIterStr = state.parameters.getString(base.push(P_ITER), null);
		if (numIterStr != null) {
			numIterations = 3;
		}

		bestGroupFitness.setStandardizedFitness(state, Double.MAX_VALUE);
		bestGroupOfGenerationFitness.setStandardizedFitness(state, Double.MAX_VALUE);
	}

}
