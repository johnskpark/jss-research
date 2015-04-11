package jss.evolution.grouping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jss.evolution.IGroupedIndividual;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.koza.KozaFitness;
import ec.util.Parameter;

/**
 * TODO javadoc.
 * @author John Park
 *
 */
public class EnsembleGroupedIndividual implements IGroupedIndividual {

	private static final long serialVersionUID = 5515931398086837728L;

	public static final String P_GROUP_SIZE = "group_size";
	public static final String P_ITER = "iteration";

	private int groupSize = 1;
	private int numIterations = 1;

	private Map<Individual, GPIndividual[][]> evalGroups = new HashMap<Individual, GPIndividual[][]>();
	private GPIndividual[] bestGroup = null;
	private KozaFitness bestGroupFitness = new KozaFitness();

	private GPIndividual[] bestGroupOfGeneration = null;
	private KozaFitness bestGroupOfGenerationFitness = new KozaFitness();

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
			groupSize = state.parameters.getInt(base.push(P_GROUP_SIZE), null);
			numIterations = state.parameters.getInt(base.push(P_ITER), null);

			bestGroupFitness.setStandardizedFitness(state, Double.MAX_VALUE);
			bestGroupOfGenerationFitness.setStandardizedFitness(state, Double.MAX_VALUE);
	}

	@Override
	public void groupIndividuals(final EvolutionState state, final int threadnum) {
		Individual[] inds = state.population.subpops[0].individuals;

		for (int i = 0; i < inds.length; i++) {
			GPIndividual[][] evalGroup = new GPIndividual[numIterations][groupSize];

			List<GPIndividual> remainingInds = new ArrayList<GPIndividual>(inds.length-1);
			for (Individual ind : inds) {
				remainingInds.add((GPIndividual) ind);
			}
			remainingInds.remove(inds[i]);

			Collections.shuffle(remainingInds, new Random(state.random[threadnum].nextLong()));

			int index = 0;
			for (int iteration = 0; iteration < numIterations; iteration++) {
				evalGroup[iteration][0] = (GPIndividual) inds[i];
				for (int count = 1; count < groupSize; count++) {
					evalGroup[iteration][count] = remainingInds.get(index);
					index++;
				}
			}

			evalGroups.put(inds[i], evalGroup);
		}
	}

	@Override
	public GPIndividual[][] getGroups(Individual ind) {
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
