package app.evolution.multitask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import app.evolution.JasimaGPIndividual;
import app.evolution.multitask.fitness.MultitaskFitnessBase;
import app.evolution.simple.JasimaSimpleProblem;
import app.simConfig.DynamicBreakdownSimConfig;
import ec.EvolutionState;
import ec.Individual;
import ec.Subpopulation;
import ec.util.Parameter;
import jasima.core.experiment.Experiment;
import jasima.core.util.Pair;

public class JasimaMultitaskProblem extends JasimaSimpleProblem {

	private static final long serialVersionUID = -37395823771748782L;

	public static final String P_NEIGHBOUR_JUMP = "neighbour-jump";

	private DynamicBreakdownSimConfig breakdownSimConfig;
	private MultitaskFitnessBase multitaskFitness;

	private IMultitaskNeighbourJump neighbourJump;

	private int numSimulation;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		breakdownSimConfig = (DynamicBreakdownSimConfig) getSimConfig();
		multitaskFitness = (MultitaskFitnessBase) getFitness();

		MultitaskEvolutionState multitaskState = (MultitaskEvolutionState) state;
		multitaskState.setSimConfig(breakdownSimConfig);
		multitaskState.setNumTasks(breakdownSimConfig.getNumScenarios());

		neighbourJump = (IMultitaskNeighbourJump) state.parameters.getInstanceForParameter(base.push(P_NEIGHBOUR_JUMP), null, IMultitaskNeighbourJump.class);
		neighbourJump.setup(state, base);
	}

	@Override
	public void prepareToEvaluate(final EvolutionState state, final int threadnum) {
		super.prepareToEvaluate(state, threadnum);

		MultitaskEvolutionState multitaskState = (MultitaskEvolutionState) state;

		if (state.generation == 0) {
			int numSubpops = multitaskState.population.subpops.length;
			int numTasks = multitaskState.getNumTasks();

			@SuppressWarnings("unchecked")
			List<Integer>[][] indsPerTask = new List[numSubpops][numTasks];
			for (int i = 0; i < numSubpops; i++) {
				for (int j = 0; j < numTasks; j++) {
					indsPerTask[i][j] = new ArrayList<>();
				}
			}
			multitaskState.setIndsPerTask(indsPerTask);
		}

		for (int i = 0; i < state.population.subpops.length; i++) {
			Subpopulation subpop = state.population.subpops[i];

			Arrays.stream(subpop.individuals).forEach(x -> ((JasimaMultitaskIndividual) x).setNumTasks(multitaskState.getNumTasks()));
		}

		numSimulation = 0;
	}

	@Override
	public void finishEvaluating(final EvolutionState state, final int threadnum) {
		neighbourJump.preprocessing(state, threadnum);

		for (int i = 0; i < state.population.subpops.length; i++) {
			Individual[] inds = state.population.subpops[i].individuals;
			for (int j = 0; j < inds.length; j++) {
				JasimaMultitaskIndividual multitaskInd = (JasimaMultitaskIndividual) inds[j];

				if (multitaskInd.getAssignedTask() != JasimaMultitaskIndividual.NO_TASK_SET) {
					applyToNeighbours(state, i, multitaskInd, threadnum);
				}

				multitaskFitness.setFitness(state, breakdownSimConfig, multitaskInd);
				multitaskInd.evaluated = true;
			}
		}

		JasimaMultitaskStatistics stats = (JasimaMultitaskStatistics) state.statistics;

		state.output.message("Number of times the simulation has been used: " + numSimulation);
		state.output.println("Generation " + state.generation + " simulation use count: " + numSimulation, stats.statisticslog);

		super.finishEvaluating(state, threadnum);
	}

	@Override
	public void evaluate(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		configureRule(state,
				getRule(),
				getTracker(),
				new Individual[] {ind},
				new int[] {subpopulation},
				threadnum);

		JasimaMultitaskIndividual multitaskInd = (JasimaMultitaskIndividual) ind;
		int task = multitaskInd.getAssignedTask();

		if (task != JasimaMultitaskIndividual.NO_TASK_SET) {
			// Evaluate for the specific assigned task.
			evaluateForTask(state, task, multitaskInd, subpopulation, threadnum);
		} else {
			// Evaluate for the entire problem domain.
			evaluateForDomain(state, multitaskInd, subpopulation, threadnum);
		}
	}

	public void evaluateForDomain(final EvolutionState state,
			final JasimaMultitaskIndividual ind,
			final int subpopulation,
			final int threadnum) {
		initialiseTracker(getTracker());

		for (int task = 0; task < breakdownSimConfig.getNumScenarios(); task++) {
			List<Integer> indices = breakdownSimConfig.getIndicesForScenario(task);

			for (int i = 0; i < indices.size(); i++) {
				int simConfigIndex = indices.get(i);

				Experiment experiment = getExperiment(state, getRule(), simConfigIndex, getSimConfig(), getWorkStationListeners(), getTracker());
				experiment.runExperiment();

				getFitness().accumulateFitness(simConfigIndex, getSimConfig(), (JasimaGPIndividual) ind, experiment.getResults());

				clearForExperiment(getWorkStationListeners());

				numSimulation++;
			}

			multitaskFitness.setTaskFitness(state, task, getSimConfig(), ind);
			multitaskFitness.clear();
		}

		clearForRun(getTracker());

		ind.evaluated = true;
	}

	public void evaluateForTask(final EvolutionState state,
			final int task,
			final JasimaMultitaskIndividual ind,
			final int subpopulation,
			final int threadnum) {
		initialiseTracker(getTracker());

		List<Integer> indices = breakdownSimConfig.getIndicesForScenario(task);

		for (int i = 0; i < indices.size(); i++) {
			int index = indices.get(i);

			Experiment experiment = getExperiment(state, getRule(), index, getSimConfig(), getWorkStationListeners(), getTracker());
			experiment.runExperiment();

			getFitness().accumulateFitness(i, getSimConfig(), (JasimaGPIndividual) ind, experiment.getResults());

			clearForExperiment(getWorkStationListeners());

			numSimulation++;
		}

		multitaskFitness.setTaskFitness(state, task, getSimConfig(), ind);
		multitaskFitness.clear();

		clearForRun(getTracker());
	}

	private void applyToNeighbours(final EvolutionState state,
			final int subpopulation,
			final JasimaMultitaskIndividual ind,
			final int threadnum) {
		Queue<Pair<Integer, Integer>> neighbourQueue = new LinkedList<>();

		// Put everything in a queue.
		int assignedTask = ind.getAssignedTask();
		addNeighbours(neighbourQueue, assignedTask);

		while (!neighbourQueue.isEmpty()) {
			Pair<Integer, Integer> taskPair = neighbourQueue.poll();

			// Ignore tasks that have already been evaluated.
			if (ind.getTaskFitness(taskPair.b) != JasimaMultitaskIndividual.NOT_SET) {
				continue;
			}

			if (neighbourJump.jumpToNeighbour(state, subpopulation, taskPair.a, taskPair.b, ind, threadnum)) {
				evaluateForTask(state, taskPair.b, ind, subpopulation, threadnum);

				// Add the individual to the task.
				neighbourJump.addIndividualToTask(state, subpopulation, taskPair.b, ind, threadnum);

				// Add the neighbours of the neighbours.
				addNeighbours(neighbourQueue, taskPair.b);
			}
		}
	}

	private void addNeighbours(Queue<Pair<Integer, Integer>> queue, int task) {
		List<Integer> neighbours = breakdownSimConfig.getNeighbourScenarios(task);

		for (int i = 0; i < neighbours.size(); i++) {
			queue.offer(new Pair<Integer, Integer>(task, neighbours.get(i)));
		}
	}

}
