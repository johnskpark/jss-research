package jss.evolution.sample;

import jss.IResult;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import ec.gp.koza.KozaFitness;
import ec.util.Parameter;

public class TwoStaticJSSProblem extends GPProblem {

	private static final long serialVersionUID = -9179316619823952437L;

	private static final int DATASET_SEED = 15;

	private TwoStaticJSSDataset dataset = new TwoStaticJSSDataset(DATASET_SEED);
	private BasicSolver solver = new BasicSolver();

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		input = (BasicData)state.parameters.getInstanceForParameterEq(base.push(P_DATA), null, BasicData.class);
		input.setup(state, base.push(P_DATA));
	}

	@Override
	public void evaluate(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		if (!ind.evaluated) {
			// Check to make sure that the individual is a GPIndividual and uses KozaFitness.
			checkIndividual(state, ind);

			BasicStatistics stats = new BasicStatistics();

			solver.setRule(new BasicRule(state, (GPIndividual)ind, subpopulation, threadnum, (BasicData)input));

			for (BasicInstance problem : dataset.getProblems()) {
				IResult solution = solver.getSolution(problem);

				stats.addSolution(problem, solution);
			}

			((KozaFitness)ind.fitness).setStandardizedFitness(state, stats.getAverageMakespan());

			ind.evaluated = true;
		}
	}

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
		TwoStaticJSSProblem newObject = (TwoStaticJSSProblem)super.clone();

		newObject.input = (BasicData)input.clone();
		newObject.dataset = dataset;
		newObject.solver = solver;

		return newObject;
	}
}
