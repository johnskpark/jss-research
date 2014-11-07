package jss.evolution;

import java.util.ArrayList;
import java.util.List;

import jss.IDataset;
import jss.IProblemInstance;
import jss.IResult;
import jss.ProblemSize;
import jss.evolution.solvers.PriorityTracker;
import jss.problem.Statistics;
import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.coevolve.GroupedProblemForm;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import ec.gp.koza.KozaFitness;
import ec.util.Parameter;

/**
 * TODO in the future, try and see if I can group this along with the
 * simple problem form and make a standardised problem form.
 * @author parkjohn
 *
 */
public class JSSGPGroupedProblem extends GPProblem implements GroupedProblemForm {

	private static final long serialVersionUID = 7483010104507824649L;

	public static final String P_SHOULD_SET_CONTEXT = "set-context";
	private boolean shouldSetContext;

	public static final String P_INSTANCES = "instances";
	public static final String P_SOLVER = "solver";

	private IDataset dataset;
	private JSSGPSolver solver;

	private ProblemSize problemSize;
	private boolean problemSizeSet = false;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// Load whether we should set context or not.
		shouldSetContext = state.parameters.getBoolean(base.push(P_SHOULD_SET_CONTEXT), null, true);

		// Setup the GPData.
		input = (JSSGPData) state.parameters.getInstanceForParameterEq(base.push(P_DATA), null, JSSGPData.class);
		input.setup(state, base.push(P_DATA));

		// Setup the dataset and the solver.
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
	public void evaluate(EvolutionState state, Individual ind,
			int subpopulation, int threadnum) {
		if (!ind.evaluated) {
			// Check to make sure that the individual is a GPIndividual and uses KozaFitness.
			checkInvariance(state, ind);

			Statistics stats = new Statistics();

			JSSGPConfiguration config = new JSSGPConfiguration();
			config.setState(state);
			config.setIndividuals(new GPIndividual[]{(GPIndividual)ind});
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

			((KozaFitness)ind.fitness).setStandardizedFitness(state, stats.getAverageMakespan());

			ind.evaluated = true;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void evaluate(final EvolutionState state,
			final Individual[] ind,
			final boolean[] updateFitness,
			final boolean countVictoriesOnly,
			final int[] subpops,
			final int threadnum) {
		checkInvariance(state, ind);

		Statistics stats = new Statistics();

		GPIndividual[] gpInds = new GPIndividual[ind.length];
		for (int i = 0; i < ind.length; i++) {
			gpInds[i] = (GPIndividual)ind[i];
		}

		JSSGPConfiguration config = new JSSGPConfiguration();
		config.setState(state);
		config.setIndividuals(gpInds);
		config.setSubpopulations(subpops);
		config.setThreadnum(threadnum);
		config.setData((JSSGPData)input);

		// TODO hack code. Fix later.
//		PriorityTracker[] trackers = new PriorityTracker[gpInds.length];
//		for (int i = 0; i < ind.length; i++) {
//			trackers[i] = new PriorityTracker();
//		}
//		config.setTrackers(trackers);

		solver.setGPConfiguration(config);

		List<IProblemInstance> trainingSet = (problemSizeSet) ?
				dataset.getTraining(problemSize) : dataset.getProblems();

		for (IProblemInstance problem : trainingSet) {
			IResult solution = solver.getSolution(problem);

			stats.addSolution(problem, solution);

			// TODO temporary code.
//			double[] p = calculatePenalties(trackers);
//			for (int i = 0; i < ind.length; i++) {
//				penalties[i] += p[i] / trainingSet.size();
//				trackers[i].clear();
//			}
		}

		// TODO make this generic.

		for (int i = 0; i < ind.length; i++) {
			double fitness = stats.getAverageMakespan();

			if (updateFitness[i]) {
				GPIndividual gpInd = gpInds[i];

				int len = gpInd.fitness.trials.size();

				if (len == 0) {
					if (shouldSetContext) {
						gpInd.fitness.setContext(ind, i);
					}

					gpInd.fitness.trials.add(new Double(fitness));
				} else if ((Double)gpInd.fitness.trials.get(0) < fitness) {
					if (shouldSetContext) {
						gpInd.fitness.setContext(ind, i);
					}

					Object t = gpInd.fitness.trials.get(0);
					gpInd.fitness.trials.set(0, fitness);
					gpInd.fitness.trials.add(t);
				}

				((KozaFitness)gpInd.fitness).setStandardizedFitness(state, fitness);
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
		JSSGPGroupedProblem newObject = (JSSGPGroupedProblem)super.clone();

		newObject.input = (JSSGPData)input.clone();
		newObject.dataset = dataset;
		newObject.solver = solver;

		return newObject;
	}

	// TODO temp code.
	private double[] calculatePenalties(PriorityTracker[] trackers) {
		double[] penalties = new double[trackers.length];

		for (int i = 0; i < penalties.length; i++) {
			List<Double> thisPriorities = trackers[i].getPriorities();

			double[] thisSigmoids = new double[thisPriorities.size()];
			for (int j = 0; j < thisPriorities.size(); j++) {
				thisSigmoids[j] = 1.0 / (1.0 + Math.exp(-thisPriorities.get(j)));
			}

			for (int j = 0; j < penalties.length; j++) {
				if (i == j) {
					continue;
				}

				List<Double> otherPriorities = trackers[j].getPriorities();

				double[] otherSigmoids = new double[otherPriorities.size()];
				for (int k = 0; k < otherPriorities.size(); k++) {
					otherSigmoids[k] = 1.0 / (1.0 + Math.exp(-otherPriorities.get(k)));

					penalties[i] += (thisSigmoids[k] - otherSigmoids[k]) *
							(thisSigmoids[k] - otherSigmoids[k]);
				}
			}

			penalties[i] = 1 - penalties[i] / (thisPriorities.size() *
					(penalties.length - 1));
		}

		return penalties;
	}

}
