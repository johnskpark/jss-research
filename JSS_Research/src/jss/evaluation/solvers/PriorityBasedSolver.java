package jss.evaluation.solvers;

import jss.evaluation.JSSEvalConfiguration;
import jss.evaluation.JSSEvalSolver;
import jss.evaluation.solvers.PriorityBasedDR;
import jss.problem.CompletelyReactiveSolver;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class PriorityBasedSolver extends JSSEvalSolver {

	/**
	 * TODO javadoc.
	 */
	public PriorityBasedSolver() {
		super();
	}

	@Override
	public void setChildConfiguration(JSSEvalConfiguration config) {
		CompletelyReactiveSolver solver = new CompletelyReactiveSolver();
		solver.setRule(new PriorityBasedDR(config.getRules().get(0)));

		setSolver(solver);
	}
}
