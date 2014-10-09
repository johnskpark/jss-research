package jss.evolution;

import java.util.List;

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

public class JSSGPProblem extends GPProblem {

	private static final long serialVersionUID = -3817123526020178300L;

	private static final String P_INSTANCES = "instances";
	private static final String P_SOLVER = "solver";

	private IDataset dataset;
	private JSSGPSolver solver;

	private ProblemSize problemSize;
	private boolean problemSizeSet = false;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// Setup the GPData
		input = (JSSGPData) state.parameters.getInstanceForParameterEq(base.push(P_DATA), null, JSSGPData.class);
		input.setup(state, base.push(P_DATA));

		// Setup the dataset and the solver
		dataset = (IDataset) state.parameters.getInstanceForParameterEq(base.push(P_INSTANCES), null, IDataset.class);
		solver = (JSSGPSolver) state.parameters.getInstanceForParameterEq(base.push(P_SOLVER), null, JSSGPSolver.class);

		// Set the problem size used for the training set.
		String problemSizeStr = state.parameters.getString(base.push("TODO"), null);
		if (problemSizeStr != null) {
			problemSize = ProblemSize.strToProblemSize(problemSizeStr);
			problemSizeSet = true;
		}
	}

	@Override
	public void evaluate(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		if (!ind.evaluated) {
			// Check to make sure that the individual is a GPIndividual and uses KozaFitness.
			checkIndividual(state, ind);

			Statistics stats = new Statistics();

			JSSGPConfiguration config = new JSSGPConfiguration();
			config.setState(state);
			config.setIndividual((GPIndividual)ind);
			config.setSubpopulation(subpopulation);
			config.setThreadnum(threadnum);
			config.setData((JSSGPData)input);

			solver.setGPConfiguration(config);

			List<IProblemInstance> trainingSet = (problemSizeSet) ?
					dataset.getTraining(problemSize) : dataset.getProblems();
			for (IProblemInstance problem : trainingSet) {
				IResult solution = solver.getSolution(problem);

				stats.addSolution(problem, solution);
			}

			((KozaFitness)ind.fitness).setStandardizedFitness(state, stats.getAverageMakespan());

			ind.evaluated = true;
		}
	}

	// Check the individual for invariance. Each individual must be a GPIndividual,
	// and the fitness must be KozaFitness.
	private void checkIndividual(final EvolutionState state, final Individual ind) {
		if (!(ind instanceof GPIndividual)) {
			state.output.error("The individual must be an instance of GPIndividual");
		}
		if (!(ind.fitness instanceof KozaFitness)) {
			state.output.error("The individual's fitness must be an instance of KozaFitness");
		}
	}

	@Override
	public Object clone() {
		JSSGPProblem newObject = (JSSGPProblem)super.clone();

		newObject.input = (JSSGPData)input.clone();
		newObject.dataset = dataset;
		newObject.solver = solver;

		return newObject;
	}
}
