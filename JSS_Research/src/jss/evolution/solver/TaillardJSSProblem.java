package jss.evolution.solver;

import jss.IDataset;
import jss.IProblemInstance;
import jss.IResult;
import jss.evolution.JSSGPData;
import jss.problem.CompletelyReactiveSolver;
import jss.problem.Statistics;
import jss.problem.static_problem.taillard_dataset.TaillardDataset;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import ec.gp.koza.KozaFitness;
import ec.util.Parameter;

/**
 * TODO javadoc. This will probably be merged with the TwoStaticJSSProblem to form a single class later down the line.
 *
 * @author parkjohn
 *
 */
public class TaillardJSSProblem extends GPProblem {

	private static final long serialVersionUID = -9179316619823952437L;

	private IDataset dataset = new TaillardDataset();
	private CompletelyReactiveSolver solver = new CompletelyReactiveSolver();

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		input = (JSSGPData)state.parameters.getInstanceForParameterEq(base.push(P_DATA), null, JSSGPData.class);
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

			Statistics stats = new Statistics();

			solver.setRule(new PriorityBasedDR(state, new GPIndividual[]{(GPIndividual)ind}, threadnum, (JSSGPData)input));

			for (IProblemInstance problem : dataset.getProblems()) {
				IResult solution = solver.getSolution(problem);

				stats.addSolution(problem, solution);
			}

			((KozaFitness)ind.fitness).setStandardizedFitness(state, stats.getAverageMakespan());

			ind.evaluated = true;
		}
	}

	// TODO doc
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
		TaillardJSSProblem newObject = (TaillardJSSProblem)super.clone();

		newObject.input = (JSSGPData)input.clone();
		newObject.dataset = dataset;
		newObject.solver = solver;

		return newObject;
	}
}