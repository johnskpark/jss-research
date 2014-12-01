package jss.evolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 *
 * @author parkjohn
 *
 */
public class JSSGPTestProblem extends GPProblem {

	private static final long serialVersionUID = -3817123526020178300L;

	public static final String P_SOLVER = "solver";
	public static final String P_INSTANCES = "instances";
	public static final String P_FITNESS = "fitness";
	public static final String P_SIZE = "size";

	public static final String P_GROUP = "group";
	public static final String P_ITER = "iteration";

	public static final String TRACKER_DATA = "tracker";

	private JSSGPSolver solver;
	private IDataset dataset;
	private ISimpleFitness fitness;

	private ProblemSize problemSize;
	private boolean problemSizeSet = false;

	private int groupSize = 3;
	private int numIterations = 3;

	private Map<GPIndividual, List<GPIndividual[]>> evalGroups = new HashMap<GPIndividual, List<GPIndividual[]>>();

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

		// Set the problem size used for the training set.
		String problemSizeStr = state.parameters.getString(base.push(P_SIZE), null);
		if (problemSizeStr != null) {
			problemSize = ProblemSize.strToProblemSize(problemSizeStr);
			problemSizeSet = true;
		}

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
	}

	@Override
	public void prepareToEvaluate(final EvolutionState state, final int threadnum) {
		Individual[] inds = state.population.subpops[0].individuals;
		for (int i = 0; i < inds.length; i++) {
			evalGroups.put((GPIndividual) inds[i], new ArrayList<GPIndividual[]>());

			List<Individual> remainingInds = Arrays.asList(inds);

			int iteration = 0;
			while (iteration < numIterations && !remainingInds.isEmpty()) {
				GPIndividual[] indGroup = new GPIndividual[groupSize];
				indGroup[0] = (GPIndividual) inds[i];

				int count = 1;
				while (count < groupSize && !remainingInds.isEmpty()) {
					int index = state.random[threadnum].nextInt(remainingInds.size());
					indGroup[count] = (GPIndividual) remainingInds.remove(index);

					count++;
				}
				evalGroups.get(inds[i]).add(indGroup);

				iteration++;
			}
		}
	}

	@Override
	public void finishEvaluating(final EvolutionState state, final int threadnum) {
		evalGroups.clear();
	}

	@Override
	public void evaluate(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		if (!ind.evaluated) {
			// Check to make sure that the individual is a GPIndividual and uses KozaFitness.
			checkInvariance(state, ind);

			Statistics stats = new Statistics();

			List<GPIndividual[]> indGroups = evalGroups.get(ind);
			for (GPIndividual[] indGroup : indGroups) {
				PriorityTracker tracker = new PriorityTracker();
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
			}

			((KozaFitness)ind.fitness).setStandardizedFitness(state, fitness.getFitness(stats));

			ind.evaluated = true;
		}
	}

	// Check the individual for invariance. Each individual must be a GPIndividual,
	// and the fitness must be KozaFitness.
	private void checkInvariance(final EvolutionState state, final Individual ind) {
		if (!(ind instanceof GPIndividual)) {
			state.output.error("The individual must be an instance of GPIndividual");
		}
		if (!(ind.fitness instanceof KozaFitness)) {
			state.output.error("The individual's fitness must be an instance of KozaFitness");
		}
	}

	@Override
	public Object clone() {
		JSSGPTestProblem newObject = (JSSGPTestProblem)super.clone();

		newObject.input = (JSSGPData)input.clone();
		newObject.dataset = dataset;
		newObject.solver = solver;

		return newObject;
	}

}
