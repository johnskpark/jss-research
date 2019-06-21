package app.evolution.multitask;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import ec.EvolutionState;
import ec.Individual;
import ec.simple.SimpleProblemForm;
import ec.simple.SimpleStatistics;

public class JasimaMultitaskStatistics extends SimpleStatistics {

	private static final long serialVersionUID = 9036462995622832702L;

	private JasimaMultitaskIndividual[][] bestIndPerTask = null;
	private JasimaMultitaskIndividual[] bestIndOfGen = null;

	private double[][] bestTaskFitnesses;
	private double[][] worstTaskFitnesses;
	private double[][] meanTaskFitnesses;
	private double[][] medianTaskFitnesses;
	private MedianCalculator[][] medianCalculator;
	private int[][] numIndsPerTask;

	@Override
	public void preEvaluationStatistics(final EvolutionState state) {
		super.preEvaluationStatistics(state);

		MultitaskEvolutionState multitaskState = (MultitaskEvolutionState) state;

		int numSubpops = multitaskState.population.subpops.length;
		int numTasks = multitaskState.getNumTasks();

		bestIndPerTask = new JasimaMultitaskIndividual[numSubpops][numTasks];
		bestIndOfGen = new JasimaMultitaskIndividual[numSubpops];

		bestTaskFitnesses = new double[numSubpops][numTasks];
		worstTaskFitnesses = new double[numSubpops][numTasks];
		meanTaskFitnesses = new double[numSubpops][numTasks];
		medianTaskFitnesses = new double[numSubpops][numTasks];
		medianCalculator = new MedianCalculator[numSubpops][numTasks];
		numIndsPerTask = new int[numSubpops][numTasks];
	}

	@Override
	public void postEvaluationStatistics(final EvolutionState state) {
		super.postEvaluationStatistics(state);

		MultitaskEvolutionState multitaskState = (MultitaskEvolutionState) state;

		int numSubpops = multitaskState.population.subpops.length;
		int numTasks = multitaskState.getNumTasks();

		// Collect the standarded fitnesses of the individuals.
		for (int i = 0; i < numSubpops; i++) {
			Individual[] inds = state.population.subpops[i].individuals;

			for (int j = 0; j < numTasks; j++) {
				bestTaskFitnesses[i][j] = Double.POSITIVE_INFINITY;
				worstTaskFitnesses[i][j] = 0.0;
				meanTaskFitnesses[i][j] = 0.0;
				medianTaskFitnesses[i][j] = 0.0;
				medianCalculator[i][j] = new MedianCalculator();
				numIndsPerTask[i][j] = 0;
			}

			// Carry out fitness comparisons between the current individual and the best individual found so far.
			for (int j = 0; j < inds.length; j++) {
				JasimaMultitaskIndividual ind = (JasimaMultitaskIndividual) inds[j];

				if (ind.evaluated) {
					// Jasima problems use KozaFitness.
					if (bestIndOfGen[i] == null || ind.fitness.betterThan(bestIndOfGen[i].fitness)) {
						bestIndOfGen[i] = ind;
					}

					// For multitask fitness.
					for (int k = 0; k < numTasks; k++) {
						if (bestIndPerTask[i][k] == null || ind.taskFitnessBetterThan(bestIndPerTask[i][k], k)) {
							bestIndPerTask[i][k] = ind;
						}

						double taskFit = ind.getTaskFitness(k);
						if (!Double.isNaN(taskFit) && taskFit > 0) {
							bestTaskFitnesses[i][k] = Math.min(bestTaskFitnesses[i][k], taskFit);
							worstTaskFitnesses[i][k] = Math.max(worstTaskFitnesses[i][k], taskFit);
							meanTaskFitnesses[i][k] += taskFit;
							medianCalculator[i][k].insert(taskFit);
							numIndsPerTask[i][k]++;
						}
					}
				}
			}

			for (int j = 0; j < numTasks; j++) {
				meanTaskFitnesses[i][j] = 1.0 * meanTaskFitnesses[i][j] / numIndsPerTask[i][j];
				medianTaskFitnesses[i][j] = medianCalculator[i][j].getMedian();
			}

			// Print out a summary of the individual's fitnesses.
			if (doGeneration) { state.output.println("\n Best Multitask Individuals of Subpopulation " + i + ":",statisticslog); }
			for (int j = 0; j < numTasks; j++) {
				if (doGeneration) {
					state.output.println("Task: " + j, statisticslog);
					bestIndPerTask[i][j].printIndividualForHumans(state, statisticslog);

					state.output.println("Task: " + j + " best/worst/mean/median/count fitnesses: " + String.format("[%f,%f,%f,%f,%d]",
							bestTaskFitnesses[i][j],
							worstTaskFitnesses[i][j],
							meanTaskFitnesses[i][j],
							medianTaskFitnesses[i][j],
							numIndsPerTask[i][j]), statisticslog);
				}
				if (doMessage && !silentPrint) {
					state.output.message("Subpop " + i + " task " + j + " best fitness of generation" +
							(bestIndPerTask[i][j].evaluated ? " " : " (evaluated flag not set): ") +
							bestIndPerTask[i][j].fitness.fitnessToStringForHumans());
				}
			}
		}
	}

	@Override
	public void finalStatistics(final EvolutionState state, final int result) {
		bypassFinalStatistics(state, result);

		MultitaskEvolutionState multitaskState = (MultitaskEvolutionState) state;

		int numSubpops = multitaskState.population.subpops.length;
		int numTasks = multitaskState.getNumTasks();

		// For now we just print the best fitness of last generation of subpopulation i.

		if (doFinal) { state.output.println("\nBest Individual of Run:", statisticslog); }
		for (int i = 0; i < numSubpops; i++) {
			// Print out the best overall individual.
			if (doFinal) {
				state.output.println("Subpopulation " + i + ":", statisticslog);
				bestIndOfGen[i].printIndividualForHumans(state, statisticslog);
			}

			if (doMessage && !silentPrint) { state.output.message("Subpop " + i + " best fitness of run: " + bestIndOfGen[i].fitness.fitnessToStringForHumans()); }

			// finally describe the winner if there is a description
			if (doFinal && doDescription) {
				if (state.evaluator.p_problem instanceof SimpleProblemForm) {
					((SimpleProblemForm) (state.evaluator.p_problem.clone())).describe(state, bestIndOfGen[i], 0, 0, statisticslog);
				}
			}

			// Print out the best individuals per task.
			if (doFinal) { state.output.println("\nBest Multitask Individual of Subpopulation " + i + ":", statisticslog); }
			for (int j = 0; j < numTasks; j++) {
				JasimaMultitaskIndividual taskInd = bestIndPerTask[i][j];
				if (doFinal) {
					state.output.println("Task " + j + ":", statisticslog);
					taskInd.printIndividualForHumans(state, statisticslog);
				}

				if (doMessage && !silentPrint) { state.output.message("Subpop " + i + " task " + j + " best fitness of run: " + taskInd.fitness.fitnessToStringForHumans()); }
			}
		}

		// Print out the fitness and the tasks.
		if (doFinal) {
			for (int i = 0; i < numSubpops; i++) {
				state.output.println("\nPrinting out Individual Fitness for Subpopulation " + i + ":", statisticslog);

				for (int j = 0; j < state.population.subpops[i].individuals.length; j++) {
					JasimaMultitaskIndividual ind = (JasimaMultitaskIndividual) state.population.subpops[i].individuals[j];

					List<Double> fitness = ind.getTaskFitnesses();
					List<String> fitnessStr = fitness.stream().map(x -> (x == MultitaskKozaFitness.NOT_SET) ? "NA" : x+"").collect(Collectors.toList());

					state.output.println(fitnessStr.toString(), statisticslog);
				}
			}
		}
	}

	// Uses the rolling median algorithm to find the median.
	private class MedianCalculator {
		private PriorityQueue<Double> minHeap = new PriorityQueue<>(new Comparator<Double>() {
			public int compare(Double o1, Double o2) {
				if (o1 > o2) { return -1; }
				else { return 1; }
			}
		});
		private PriorityQueue<Double> maxHeap = new PriorityQueue<>(new Comparator<Double>() {
			public int compare(Double o1, Double o2) {
				if (o1 < o2) { return -1; }
				else { return 1; }
			}
		});
		private int count;

		public void insert(double taskFitness) {
			if (count == 0) {
				minHeap.offer(taskFitness);
			} else {
				// Add to the respective heap based on its comparison to the median.
				double curMedian = getMedian();
				if (taskFitness < curMedian) {
					minHeap.offer(taskFitness);
				} else if (taskFitness > curMedian) {
					maxHeap.offer(taskFitness);
				} else {
					if (minHeap.size() > maxHeap.size()) {
						maxHeap.offer(taskFitness);
					} else {
						minHeap.offer(taskFitness);
					}
				}

				// Rebalance the heaps if they are off by more than one.
				if (minHeap.size() > maxHeap.size() + 1) {
					maxHeap.offer(minHeap.poll());
				} else if (maxHeap.size() > minHeap.size() + 1) {
					minHeap.offer(maxHeap.poll());
				}
			}
			count++;
		}

		public double getMedian() {
			if (minHeap.size() > maxHeap.size()) {
				return minHeap.peek();
			} else if (minHeap.size() < maxHeap.size()) {
				return maxHeap.peek();
			} else {
				return (minHeap.peek() + maxHeap.peek()) / 2.0;
			}
		}

		public void clear() {
			minHeap.clear();
			maxHeap.clear();
			count = 0;
		}
	}


}
