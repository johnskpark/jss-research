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
import app.simConfig.ExperimentGenerator;
import app.simConfig.SimConfig;
import ec.EvolutionState;
import ec.Individual;
import ec.Subpopulation;
import ec.util.Parameter;
import jasima.core.experiment.Experiment;
import jasima.core.util.Pair;

/**
 * The main problem component for the multitask GP approach. 
 * @author John
 *
 */
public class JasimaMultitaskProblem extends JasimaSimpleProblem {

	private static final long serialVersionUID = -37395823771748782L;

	public static final String P_INIT_TASK = "init-task";
	public static final String P_NEIGHBOUR_JUMP = "neighbour-jump";

	private DynamicBreakdownSimConfig breakdownSimConfig;
	private MultitaskFitnessBase multitaskFitness;

	private IMultitaskInitTaskStrategy initTaskStrategy;
	private IMultitaskNeighbourJump neighbourJump;

	private int numSimulation;
	private Integer[] numSimPerTask;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		breakdownSimConfig = (DynamicBreakdownSimConfig) getSimConfig();
		multitaskFitness = (MultitaskFitnessBase) getFitness();

		MultitaskEvolutionState multitaskState = (MultitaskEvolutionState) state;
		multitaskState.setSimConfig(breakdownSimConfig);
		multitaskState.setNumTasks(breakdownSimConfig.getNumScenarios());

		initTaskStrategy = (IMultitaskInitTaskStrategy) state.parameters.getInstanceForParameter(base.push(P_INIT_TASK), null, IMultitaskInitTaskStrategy.class);

		neighbourJump = (IMultitaskNeighbourJump) state.parameters.getInstanceForParameter(base.push(P_NEIGHBOUR_JUMP), null, IMultitaskNeighbourJump.class);
		neighbourJump.setup(state, base);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void prepareToEvaluate(final EvolutionState state, final int threadnum) {
		super.prepareToEvaluate(state, threadnum);

		MultitaskEvolutionState multitaskState = (MultitaskEvolutionState) state;

		if (state.generation == 0) {
			int numSubpops = multitaskState.population.subpops.length;
			int numTasks = multitaskState.getNumTasks();

			List<Integer>[][] indsPerTask = new List[numSubpops][numTasks];
			List<Integer>[][] ranksPerTask = new List[numSubpops][numTasks];

			for (int i = 0; i < numSubpops; i++) {
				for (int j = 0; j < numTasks; j++) {
					indsPerTask[i][j] = new ArrayList<>();
					ranksPerTask[i][j] = new ArrayList<>();
				}
			}

			multitaskState.setIndsPerTask(indsPerTask);
			multitaskState.setRanksPerTask(ranksPerTask);

			initTaskStrategy.initTasksForInds(multitaskState);
		}

		for (int i = 0; i < state.population.subpops.length; i++) {
			Subpopulation subpop = state.population.subpops[i];

			Arrays.stream(subpop.individuals).forEach(x -> ((JasimaMultitaskIndividual) x).setNumTasks(multitaskState.getNumTasks()));
		}

		numSimulation = 0;
		numSimPerTask = new Integer[multitaskState.getNumTasks()];
		Arrays.fill(numSimPerTask, new Integer(0));
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

		List<Integer> numSimsList = Arrays.asList(numSimPerTask);

		state.output.message("Number of times the simulation has been used for each task: " + numSimsList.toString());
		state.output.println("Generation " + state.generation + " simulation use count per task: " + numSimsList.toString(), stats.statisticslog);

		super.finishEvaluating(state, threadnum);

		neighbourJump.clear();

		System.gc();
	}

	@Override
	public void evaluate(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
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
		configureRule(state,
				getRule(),
				getTracker(),
				new Individual[] {ind},
				new int[] {subpopulation},
				threadnum);

		initialiseTracker(getTracker());

		for (int task = 0; task < breakdownSimConfig.getNumScenarios(); task++) {
			List<Integer> indices = breakdownSimConfig.getIndicesForScenario(task);

			for (int i = 0; i < indices.size(); i++) {
				int simConfigIndex = indices.get(i);

				Experiment experiment = getExperiment(state, getRule(), simConfigIndex, getSimConfig(), getWorkStationListeners(), getTracker());
				experiment.runExperiment();

				multitaskFitness.accumulateFitness(simConfigIndex, getSimConfig(), (JasimaGPIndividual) ind, experiment.getResults());

				clearForExperiment(getWorkStationListeners());

				numSimulation++;
			}

			multitaskFitness.setTaskFitness(state, task, getSimConfig(), ind);
			multitaskFitness.clear();
		}

		clearForRun(getTracker(), multitaskFitness);

		ind.evaluated = true;
	}

	public void evaluateForTask(final EvolutionState state,
			final int task,
			final JasimaMultitaskIndividual ind,
			final int subpopulation,
			final int threadnum) {
		configureRule(state,
				getRule(),
				getTracker(),
				new Individual[] {ind},
				new int[] {subpopulation},
				threadnum);

		initialiseTracker(getTracker());

		List<Integer> indices = breakdownSimConfig.getIndicesForScenario(task);

		for (int i = 0; i < indices.size(); i++) {
			int simConfigIndex = indices.get(i);

			Experiment experiment = getExperiment(state, getRule(), simConfigIndex, getSimConfig(), getWorkStationListeners(), getTracker());
			experiment.runExperiment();

			multitaskFitness.accumulateFitness(simConfigIndex, getSimConfig(), (JasimaGPIndividual) ind, experiment.getResults());

			clearForExperiment(getWorkStationListeners());

			numSimulation++;
			if (ind.getAssignedTask() != JasimaMultitaskIndividual.NO_TASK_SET) {
				numSimPerTask[ind.getAssignedTask()]++;
			}
		}

		multitaskFitness.setTaskFitness(state, task, getSimConfig(), ind);

		clearForRun(getTracker(), multitaskFitness);
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
			if (ind.getTaskFitness(taskPair.b) != MultitaskKozaFitness.NOT_SET) {
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

	@Override
	protected void evaluateReference(SimConfig simConfig) {
		if (!hasReferenceRule()) {
			throw new RuntimeException("Cannot evaluate reference rule. Reference rule is not initialised.");
		}
		if (getReferenceInstStats().size() != 0) {
			throw new RuntimeException("The reference rule has been previously evaluated. Please clear the statistics for the reference rule beforehand.");
		}

		for (int task = 0; task < breakdownSimConfig.getNumScenarios(); task++) {
			List<Integer> indices = breakdownSimConfig.getIndicesForScenario(task);

			for (int i = 0; i < indices.size(); i++) {
				int simConfigIndex = indices.get(i);

				Experiment experiment = ExperimentGenerator.getExperiment(simConfig,
						getReferenceRule(),
						simConfigIndex);

				experiment.runExperiment();

				double result = getReferenceFitness().getFitness(simConfigIndex, simConfig, null, experiment.getResults());
				getReferenceInstStats().add(result);
			}

			simConfig.reset();
		}
	}

}
