package app.evolution.multitask.jump;

import java.util.Comparator;
import java.util.PriorityQueue;

import app.evolution.multitask.JasimaMultitaskIndividual;
import app.evolution.multitask.MultitaskKozaFitness;
import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

// Uses the median value for the jump calculation.
public class FitnessProbabilityJump3 extends FitnessProbabilityJump {

	private static final long serialVersionUID = 5953377944185011148L;

	private MedianCalculator[][] medianCalc;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
	}

	@Override
	public void preprocessing(final EvolutionState state, final int threadnum) {
		super.preprocessing(state, threadnum);
		if (state.generation == 0) {
			medianCalc = new MedianCalculator[getNumSubpops()][getNumTasks()];

			for (int i = 0; i < getNumSubpops(); i++) {
				for (int j = 0; j < getNumTasks(); j++) {
					medianCalc[i][j] = new MedianCalculator();
				}
			}
		} else {
			for (int subpop = 0; subpop < state.population.subpops.length; subpop++) {
				Individual[] inds = state.population.subpops[subpop].individuals;
				for (int i = 0; i < inds.length; i++) {
					JasimaMultitaskIndividual ind = (JasimaMultitaskIndividual) inds[i];

					for (int task = 0; task < getNumTasks(); task++) {
						if (ind.getTaskFitness(task) == MultitaskKozaFitness.NOT_SET) {
							continue;
						}

						medianCalc[subpop][task].insert(ind.getTaskFitness(task));
					}
				}
			}
		}
	}

	@Override
	public boolean jumpToNeighbour(final EvolutionState state,
			final int subpopulation,
			final int currentTask,
			final int neighbourTask,
			final JasimaMultitaskIndividual ind,
			final int threadnum) {
		// Calculate the probability from the fitness using min-max normalisation (starting from median fitness)
		// to bring the fitness value in between 0.0 and 1.0 first. All fitness below the median is ignored.
		double taskFitness = ind.getTaskFitness(currentTask);
		double minFitness = getBestIndsPerTask()[subpopulation][currentTask].getTaskFitness(currentTask);
		double medianFitness = medianCalc[subpopulation][currentTask].getMedian();

		double prob = Math.max(1.0 - (taskFitness - minFitness) / (medianFitness - minFitness), 0.0);

		probabilityOutput(state, prob, taskFitness, medianFitness, minFitness);

		return getRand().nextBoolean(prob);
	}

	@Override
	public void addIndividualToTask(final EvolutionState state,
			final int subpopulation,
			final int task,
			final JasimaMultitaskIndividual ind,
			final int threadnum) {
		super.addIndividualToTask(state, subpopulation, task, ind, threadnum);

		medianCalc[subpopulation][task].insert(ind.getTaskFitness(task));
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
				if (!minHeap.isEmpty()) {
					return (minHeap.peek() + maxHeap.peek()) / 2.0;
				} else {
					return 0.0;
				}
			}
		}

		public void clear() {
			minHeap.clear();
			maxHeap.clear();
			count = 0;
		}
	}

	public void clear() {
		for (int i = 0; i < getNumSubpops(); i++) {
			for (int j = 0; j < getNumTasks(); j++) {
				medianCalc[i][j].clear();
			}
		}
	}
}
