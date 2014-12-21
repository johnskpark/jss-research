package jss.evolution.grouping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jss.evolution.IGroupedIndividual;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.koza.KozaFitness;
import ec.util.Parameter;

/**
 * Right, let's do this. TODO javadoc sometime in the future.
 * @author John Park
 *
 */
public class EnsembleGroupedIndividual implements IGroupedIndividual {

	private static final long serialVersionUID = 5515931398086837728L;

	public static final String P_GROUP_SIZE = "group_size";
	public static final String P_ITER = "iteration";

	private int groupSize = 3;
	private int numIterations = 3;

	private Map<GPIndividual, List<GPIndividual[]>> evalGroups = new HashMap<GPIndividual, List<GPIndividual[]>>();
	private GPIndividual[] bestGroup = null;
	private KozaFitness bestGroupFitness = new KozaFitness();

	private GPIndividual[] bestGroupOfGeneration = null;
	private KozaFitness bestGroupOfGenerationFitness = new KozaFitness();

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		// Set the group size used for the individuals.
		String groupSizeStr = state.parameters.getString(base.push(P_GROUP_SIZE), null);
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

	@Override
	public void groupIndividuals(final EvolutionState state, final int threadnum) {
		Individual[] inds = state.population.subpops[0].individuals;
		
		for (int i = 0; i < inds.length; i++) {
			evalGroups.put((GPIndividual) inds[i], new ArrayList<GPIndividual[]>());

			List<GPIndividual> remainingInds = new ArrayList<GPIndividual>();
			for (int j = 0; j < inds.length; j++) {
				remainingInds.add((GPIndividual) inds[j]);
			}

			int iteration = 0;
			while (iteration < numIterations && !remainingInds.isEmpty()) {
				GPIndividual[] indGroup = new GPIndividual[groupSize];
				indGroup[0] = (GPIndividual) inds[i];

				int count = 1;
				while (count < groupSize && !remainingInds.isEmpty()) {
					int index = state.random[threadnum].nextInt(remainingInds.size());
					indGroup[count] = remainingInds.remove(index);

					count++;
				}
				evalGroups.get(inds[i]).add(indGroup);

				iteration++;
			}
		}
	}

	@Override
	public List<GPIndividual[]> getGroups(Individual ind) {
		return evalGroups.get(ind);
	}

	@Override
	public GPIndividual[] getBestGroup() {
		return bestGroup;
	}
	
	@Override
	public KozaFitness getBestGroupFitness() {
		return bestGroupFitness;
	}

	@Override
	public GPIndividual[] getBestGroupForGeneration() {
		return bestGroupOfGeneration;
	}
	
	@Override
	public KozaFitness getBestGroupForGenerationFitness() {
		return bestGroupOfGenerationFitness;
	}

	@Override
	public void updateFitness(final EvolutionState state, 
			final GPIndividual[] indGroup, 
			final double fitness) {
		// Update the best group of generation.
		if (bestGroupOfGenerationFitness.standardizedFitness() > fitness) {
			bestGroupOfGeneration = indGroup;
			bestGroupOfGenerationFitness.setStandardizedFitness(state, fitness);
		}

		// Update the overall best group.
		if (bestGroupOfGenerationFitness.betterThan(bestGroupFitness)) {
			bestGroup = bestGroupOfGeneration;
			bestGroupFitness.setStandardizedFitness(state, bestGroupOfGenerationFitness.standardizedFitness());
		}
	}

	@Override
	public void clearForGeneration(final EvolutionState state) {
		// Clear out the grouping.
		evalGroups.clear();
		bestGroupOfGeneration = null;
		bestGroupOfGenerationFitness.setStandardizedFitness(state, Double.MAX_VALUE);

	}

}
