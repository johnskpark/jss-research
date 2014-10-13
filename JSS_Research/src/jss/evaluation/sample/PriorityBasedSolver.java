package jss.evaluation.sample;

import jss.evaluation.JSSEvalConfiguration;
import jss.evaluation.JSSEvalSolver;
import jss.evaluation.sample.PriorityBasedDR;
import jss.problem.CompletelyReactiveSolver;

public class PriorityBasedSolver extends JSSEvalSolver {

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
