package app.evolution.multilevel_new;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ec.EvolutionState;
import ec.Individual;
import ec.Subpopulation;
import ec.multilevel_new.MLSStatistics;
import ec.util.Parameter;
import jasima.core.statistics.SummaryStat;

public class JasimaMultilevelNichingStatistics extends MLSStatistics implements IJasimaMultilevelFitnessListener {

	private static final long serialVersionUID = 1831159170439048338L;

	public static final int INDIVIDUAL_FITNESS = 0;
	public static final int ENSEMBLE_FITNESS = 1;
	
	private List<SummaryStat> individualFitnesses = new ArrayList<SummaryStat>();
	private List<SummaryStat> ensembleFitnesses = new ArrayList<SummaryStat>();
	 
	private List<Double> instanceDistanceSum = new ArrayList<Double>();
	private List<Integer> instanceDistanceCount = new ArrayList<Integer>();
	private Map<Individual, Double[]> individualDistanceMap = new HashMap<Individual, Double[]>();

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
	}

	@Override
	public void addFitness(int type, int index, double value) {
		// Add the instance statistics.
		if (type == INDIVIDUAL_FITNESS) {
			individualFitnesses.get(index).value(value);
		} else if (type == ENSEMBLE_FITNESS) {
			ensembleFitnesses.get(index).value(value);
		} 
	}
	
	@Override
	public void addDiversity(int type, int index, Individual[] inds, double[] distances) {
		// Add in the instance diversity
		if (type == INDIVIDUAL_FITNESS) {
			addDiversityForIndividuals(index, inds, distances);
		} else if (type == ENSEMBLE_FITNESS) {
			addDiversityForIndividuals(index, inds, distances);
		}
	}
	
	private void addDiversityForIndividuals(int index, Individual[] inds, double[] distances) {
		for (int i = 0; i < inds.length; i++) {
			Double[] instDist = individualDistanceMap.get(inds[i]);
			
			// Adjust if the distance is smaller.
			if (Double.isNaN(instDist[index])) {
				double newInstDist = distances[i];
				
				instanceDistanceSum.set(index, newInstDist);
				instanceDistanceCount.set(index, instanceDistanceCount.get(index) + 1);
				instDist[index] = distances[i];

				individualDistanceMap.put(inds[i], instDist);
			} else if (instDist[index] > distances[i]) {
				double oldInstDist = instanceDistanceSum.get(index);
				double newInstDist = oldInstDist - instDist[index] + distances[i];
				
				instanceDistanceSum.set(index, newInstDist);
				instDist[index] = distances[i];

				individualDistanceMap.put(inds[i], instDist);
			}
		}
	}

	@Override
	public void preEvaluationStatistics(final EvolutionState state) {
		super.preEvaluationStatistics(state);

		JasimaMultilevelProblem problem = (JasimaMultilevelProblem) state.evaluator.p_problem;

		int numConfigs = problem.getSimConfig().getNumConfigs();
		
		// Fill in the fitnesses.
		for (int i = 0; i < numConfigs; i++) {
			individualFitnesses.add(new SummaryStat());
			ensembleFitnesses.add(new SummaryStat());
			
			instanceDistanceSum.add(0.0);
			instanceDistanceCount.add(0);
		}
		
		Subpopulation subpop = state.population.subpops[0];
		
		// Fill in the distances.
		for (int i = 0; i < subpop.individuals.length; i++) {
			Double[] instDist = new Double[numConfigs];
			
			for (int j = 0; j < numConfigs; j++) {
				instDist[j] = Double.NaN;
			}
			
			individualDistanceMap.put(subpop.individuals[i], instDist);
		}
	}

	@Override
	public void postEvaluationStatistics(final EvolutionState state) {
		super.postEvaluationStatistics(state);

		// Carry out the statistics for the individual training instances.
		instanceStatistics(state);

		individualFitnesses.clear();
		ensembleFitnesses.clear();
		
		instanceDistanceSum.clear();
		instanceDistanceCount.clear();
		individualDistanceMap.clear();
	}

	protected void instanceStatistics(final EvolutionState state) {
		// Print out the instance statistics.
		fitnessStatistics(state, "Individual", individualFitnesses);
		fitnessStatistics(state, "Ensemble", ensembleFitnesses);

		state.output.print("Average Diversity per Instance: ", statisticsLog);
		for (int i = 0; i < instanceDistanceSum.size(); i++) {
			double avg = instanceDistanceSum.get(i) / instanceDistanceCount.get(i);
			
			if (i == 0) {
				state.output.print("" + avg, statisticsLog);
			} else {
				state.output.print(", " + avg, statisticsLog);
			}
		}
		state.output.println("", statisticsLog);
	}
	
	protected void fitnessStatistics(final EvolutionState state, String fitnessType, List<SummaryStat> fitnessStats) {
		state.output.print(fitnessType + " Fitnesses per Instance (min,avg,max): ", statisticsLog);
		
		for (int i = 0; i < fitnessStats.size(); i++) {
			SummaryStat stat = fitnessStats.get(i);
			
			if (i != 0) {
				state.output.print(", ", statisticsLog);
			}
			state.output.print(stat.min() + ", " + stat.mean() + ", " + stat.max(), statisticsLog);
		}
		state.output.println("", statisticsLog);
	}

}
