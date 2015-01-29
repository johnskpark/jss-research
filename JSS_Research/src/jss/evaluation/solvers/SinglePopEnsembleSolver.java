package jss.evaluation.solvers;

import jss.evaluation.JSSEvalConfiguration;
import jss.evaluation.JSSEvalSolver;
import jss.problem.CompletelyReactiveSolver;

public class SinglePopEnsembleSolver extends JSSEvalSolver {

	public SinglePopEnsembleSolver() {
		super();
	}

	@Override
	public void setChildConfiguration(JSSEvalConfiguration config) {
		CompletelyReactiveSolver solver = new CompletelyReactiveSolver();

		solver.setRule(new SinglePopEnsembleDR(config.getRules(),
				config.getRuleNum()));

		setSolver(solver);
	}

}
