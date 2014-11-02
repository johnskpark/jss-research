package jss.evaluation.sample;

import jss.evaluation.JSSEvalConfiguration;
import jss.evaluation.JSSEvalSolver;
import jss.problem.CompletelyReactiveSolver;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class CoopEnsembleSolver extends JSSEvalSolver {

	/**
	 * TODO javadoc.
	 */
	public CoopEnsembleSolver() {
		super();
	}

	@Override
	protected void setChildConfiguration(JSSEvalConfiguration config) {
		CompletelyReactiveSolver solver = new CompletelyReactiveSolver();

		solver.setRule(new CoopEnsembleDR(config.getRules(),
				config.getRuleNum()));

		setSolver(solver);
	}
}
