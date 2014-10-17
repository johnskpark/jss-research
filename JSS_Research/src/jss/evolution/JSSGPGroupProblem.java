package jss.evolution;

import java.util.ArrayList;
import java.util.Arrays;

import jss.IDataset;
import jss.ProblemSize;
import jss.problem.Statistics;
import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.Problem;
import ec.coevolve.GroupedProblemForm;
import ec.gp.GPIndividual;
import ec.gp.koza.KozaFitness;
import ec.util.Parameter;

public class JSSGPGroupProblem extends Problem implements GroupedProblemForm {

	private static final long serialVersionUID = 7483010104507824649L;

	public static final String P_INSTANCES = "instances";
	public static final String P_SOLVER = "solver";
	public static final String P_DATA = "data";

	private IDataset dataset;
	private JSSGPSolver solver;
	private JSSGPData input;

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

	@SuppressWarnings("rawtypes")
	@Override
	public void preprocessPopulation(final EvolutionState state,
			final Population pop,
			final boolean[] prepareForFitnessAssessment,
			final boolean countVictoriesOnly) {
		for (int i = 0; i < pop.subpops.length; i++) {
			if (prepareForFitnessAssessment[i]) {
				for (int j = 0; j < pop.subpops[i].individuals.length; j++) {
					KozaFitness fitness = (KozaFitness)pop.subpops[i].individuals[j].fitness;
					fitness.trials = new ArrayList();
				}
			}
		}
	}

	@Override
	public void postprocessPopulation(final EvolutionState state,
			final Population pop,
			final boolean[] assessFitness,
			final boolean countVictoriesOnly) {
		for (int i = 0; i < pop.subpops.length; i++ ) {
			if (assessFitness[i]) {
				for (int j = 0; j < pop.subpops[i].individuals.length; j++) {
					KozaFitness fitness = ((KozaFitness)(pop.subpops[i].individuals[j].fitness));

					// we take the minimum over the trials
					double min = Double.POSITIVE_INFINITY;
					for (int l = 0; l < fitness.trials.size(); l++) {
						min = Math.min(((Double)(fitness.trials.get(l))).doubleValue(), min);  // it'll be the first one, but whatever
					}

					fitness.setStandardizedFitness(state, min);
					pop.subpops[i].individuals[j].evaluated = true;
				}
			}
		}
	}

	@Override
	public void evaluate(final EvolutionState state,
			final Individual[] ind,
			final boolean[] updateFitness,
			final boolean countVictoriesOnly,
			final int[] subpops,
			final int threadnum) {
		checkInvariance(state, ind);

		Statistics stats = new Statistics();

		JSSGPConfiguration config = new JSSGPConfiguration();
		config.setState(state);
		config.setIndividuals((GPIndividual[])ind);
		config.setSubpopulations(subpops);
		config.setThreadnum(threadnum);
	}

	// Check the individual for invariance. Each individual must be a GPIndividual,
	// and the fitness must be KozaFitness.
	private void checkInvariance(final EvolutionState state, final Individual[] ind) {
        if (ind.length == 0) {
            state.output.fatal("Number of individuals provided to CoevolutionaryECSuite is 0!");
        }
        if (ind.length == 1) {
            state.output.warnOnce("Coevolution used, but number of individuals provided to CoevolutionaryECSuite is 1.");
        }

        for (int i = 0; i < ind.length; i++) {
			if (!(ind[i] instanceof GPIndividual)) {
				state.output.error("The individual must be an instance of GPIndividual");
			}
			if (!(ind[i].fitness instanceof KozaFitness)) {
				state.output.error("The individual's fitness must be an instance of KozaFitness");
			}
        }

        state.output.exitIfErrors();
	}

	@Override
	public Object clone() {
		JSSGPGroupProblem newObject = (JSSGPGroupProblem)super.clone();

		newObject.input = (JSSGPData)input.clone();
		newObject.dataset = dataset;
		newObject.solver = solver;

		return newObject;
	}

}
