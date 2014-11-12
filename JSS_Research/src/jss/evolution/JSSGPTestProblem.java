package jss.evolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jss.IDataset;
import jss.IProblemInstance;
import jss.IResult;
import jss.ProblemSize;
import jss.problem.Statistics;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import ec.gp.koza.KozaFitness;
import ec.util.Parameter;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class JSSGPTestProblem extends GPProblem {

	private static final long serialVersionUID = -3817123526020178300L;

	public static final String P_INSTANCES = "instances";
	public static final String P_SOLVER = "solver";
	public static final String P_FITNESS = "fitness";
	public static final String P_SIZE = "size";

	private JSSGPSolver solver;
	private IDataset dataset;
	private IFitness fitness;

	private ProblemSize problemSize;
	private boolean problemSizeSet = false;

	public static final int GROUP_SIZE = 3;

	private List<Set<Individual>> indGroups;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// Setup the GPData
		input = (JSSGPData) state.parameters.getInstanceForParameterEq(base.push(P_DATA), null, JSSGPData.class);
		input.setup(state, base.push(P_DATA));

		// Setup the dataset and the solver
		solver = (JSSGPSolver) state.parameters.getInstanceForParameterEq(base.push(P_SOLVER), null, JSSGPSolver.class);
		dataset = (IDataset) state.parameters.getInstanceForParameterEq(base.push(P_INSTANCES), null, IDataset.class);
		fitness = (IFitness) state.parameters.getInstanceForParameterEq(base.push(P_FITNESS), null, IFitness.class);

		// Set the problem size used for the training set.
		String problemSizeStr = state.parameters.getString(base.push(P_SIZE), null);
		if (problemSizeStr != null) {
			problemSize = ProblemSize.strToProblemSize(problemSizeStr);
			problemSizeSet = true;
		}
	}

	@Override
	public void prepareToEvaluate(final EvolutionState state, final int threadnum) {
		indGroups = new ArrayList<Set<Individual>>();

		// Will assume that the problem will only consist of single population of individuals.
		List<Individual> inds = Arrays.asList(state.population.subpops[0].individuals);
		while (!inds.isEmpty()) {
			Set<Individual> set = new HashSet<Individual>();
			set.add(inds.remove(0));

			for (int i = 1; i < GROUP_SIZE && inds.isEmpty(); i++) {
				// Selects the individual randomly.
				int index = state.random[threadnum].nextInt(inds.size());
				set.add(inds.remove(index));
			}

			indGroups.add(set);
		}
	}

	@Override
	public void finishEvaluating(final EvolutionState state, final int threadnum) {
		if (state.generation == state.numGenerations - 1) {
			// TODO get the top group performers.
		}
	}

	@Override
	public void evaluate(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		if (!ind.evaluated) {
			// Check to make sure that the individual is a GPIndividual and uses KozaFitness.
			checkInvariance(state, ind);

			Set<Individual> indGroup = getIndGroup(ind);

			GPIndividual[] gpInds = new GPIndividual[indGroup.size()];

			Iterator<Individual> iter = indGroup.iterator();
			for (int i = 0; i < indGroup.size() && iter.hasNext(); i++) {
				gpInds[i] = (GPIndividual)iter.next();
			}

			Statistics stats = new Statistics();

			JSSGPConfiguration config = new JSSGPConfiguration();
			config.setState(state);
			config.setIndividuals(gpInds);
			config.setSubpopulations(new int[]{subpopulation});
			config.setThreadnum(threadnum);
			config.setData((JSSGPData)input);

			solver.setGPConfiguration(config);

			List<IProblemInstance> trainingSet = (problemSizeSet) ?
					dataset.getTraining(problemSize) : dataset.getProblems();
			for (IProblemInstance problem : trainingSet) {
				IResult solution = solver.getSolution(problem);

				stats.addSolution(problem, solution);
			}

			for (Individual groupedInd : indGroup) {
				((KozaFitness)groupedInd.fitness).setStandardizedFitness(state, fitness.getFitness(stats));
				groupedInd.evaluated = true;
			}
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

	// Get the grouping that the individual is part of.
	private Set<Individual> getIndGroup(Individual ind) {
		for (Set<Individual> group : indGroups) {
			if (group.contains(ind)) {
				return group;
			}
		}
		return null;
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
