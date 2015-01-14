package jss.evolution;

import java.util.List;

import jss.IDataset;
import jss.IProblemInstance;
import jss.IResult;
import jss.ProblemSize;
import jss.evolution.statistic_data.PenaltyData;
import jss.evolution.tracker.PriorityTracker;
import jss.problem.Statistics;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import ec.gp.koza.KozaFitness;
import ec.util.Parameter;

/**
 * TODO javadoc.
 * TODO try to incorporate this into the standardised format.
 * I will try to get the grouping into this further.
 *
 * @author parkjohn
 *
 */
public class JSSGPGroupedProblem extends GPProblem {

	private static final long serialVersionUID = -3817123526020178300L;

	public static final String P_SOLVER = "solver";
	public static final String P_FITNESS = "fitness";
	public static final String P_SIZE = "size";

	public static final String P_INSTANCES = "instances";
	public static final String P_INSTANCES_SEED = "instances_seed";

	public static final String P_GROUP = "group";
	public static final String P_TRACKER = "tracker";

	public static final String TRACKER_DATA = "tracker";

	private JSSGPSolver solver;
	private IDataset dataset;
	private ISimpleFitness fitness;

	private ProblemSize problemSize;
	private boolean problemSizeSet = false;

	private IGroupedIndividual individualGrouping = null;
	private PriorityTracker tracker = null;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// Setup the GPData
		input = (JSSGPData) state.parameters.getInstanceForParameterEq(base.push(P_DATA), null, JSSGPData.class);
		input.setup(state, base.push(P_DATA));

		// Setup the dataset and the solver
		solver = (JSSGPSolver) state.parameters.getInstanceForParameterEq(base.push(P_SOLVER), null, JSSGPSolver.class);
		dataset = (IDataset) state.parameters.getInstanceForParameterEq(base.push(P_INSTANCES), null, IDataset.class);
		fitness = (ISimpleFitness) state.parameters.getInstanceForParameterEq(base.push(P_FITNESS), null, ISimpleFitness.class);

		String datasetSeedStr = state.parameters.getString(base.push(P_INSTANCES_SEED), null);
		if (datasetSeedStr != null) {
			dataset.setSeed(Long.parseLong(datasetSeedStr));
		}
		dataset.generateDataset();

		// Set the problem size used for the training set.
		String problemSizeStr = state.parameters.getString(base.push(P_SIZE), null);
		if (problemSizeStr != null) {
			problemSize = ProblemSize.strToProblemSize(problemSizeStr);
			problemSizeSet = true;
		}

		// Set the grouping used to group the individuals together
		individualGrouping = (IGroupedIndividual) state.parameters.getInstanceForParameterEq(base.push(P_GROUP), null, IGroupedIndividual.class);
		individualGrouping.setup(state, base);

		// Set the tracker that is used to calculate the penalties for the individuals
		tracker = (PriorityTracker) state.parameters.getInstanceForParameterEq(base.push(P_TRACKER), null, PriorityTracker.class);
	}

	public IGroupedIndividual getIndividualGrouping() {
		return individualGrouping;
	}

	@Override
	public void prepareToEvaluate(final EvolutionState state, final int threadnum) {
		individualGrouping.clearForGeneration(state);

		individualGrouping.groupIndividuals(state, threadnum);
	}

	@Override
	public void finishEvaluating(final EvolutionState state, final int threadnum) {
		KozaFitness groupFitness = individualGrouping.getBestGroupForGenerationFitness();

		// Print out the best ensemble group of generation that was evaluated.
		state.output.message("Best ensemble fitness of generation " + groupFitness.fitnessToStringForHumans());
	}

	@Override
	public void evaluate(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		if (!ind.evaluated) {
			long startTime = System.currentTimeMillis();

			Statistics stats = new Statistics();
			stats.addData(TRACKER_DATA, new PenaltyData());

			List<GPIndividual[]> indGroups = individualGrouping.getGroups(ind);
			for (GPIndividual[] indGroup : indGroups) {
				tracker.loadIndividuals(indGroup);

				JSSGPConfiguration config = new JSSGPConfiguration();
				config.setState(state);
				config.setIndividuals(indGroup);
				config.setSubpopulations(new int[]{subpopulation});
				config.setThreadnum(threadnum);
				config.setData((JSSGPData)input);
				config.setTracker(tracker);

				solver.setGPConfiguration(config);

				List<IProblemInstance> trainingSet = (problemSizeSet) ?
						dataset.getTraining(problemSize) : dataset.getProblems();

				for (IProblemInstance problem : trainingSet) {
					IResult solution = solver.getSolution(problem);

					stats.addSolution(problem, solution);
					((PenaltyData) stats.getData(TRACKER_DATA)).addPenalties(tracker.getPenalties());

					tracker.clear();
				}

				double groupFitness = fitness.getFitness(stats);

				individualGrouping.updateFitness(state, indGroup, groupFitness);
			}

			((KozaFitness)ind.fitness).setStandardizedFitness(state, fitness.getFitness(stats));

			ind.evaluated = true;

			long endTime = System.currentTimeMillis();

			long timeDiff = endTime - startTime;

			System.out.printf("%d\n", timeDiff);
		}
	}

	@Override
	public Object clone() {
		JSSGPGroupedProblem newObject = (JSSGPGroupedProblem)super.clone();

		newObject.input = (JSSGPData)input.clone();
		newObject.dataset = dataset;
		newObject.solver = solver;

		return newObject;
	}

}
