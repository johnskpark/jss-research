package jss.evaluation.solvers;

import jss.IllegalConfigurationException;
import jss.evaluation.JSSEvalConfiguration;
import jss.evaluation.JSSEvalSolver;
import jss.problem.CompletelyReactiveSolver;

public class CoopTwoRuleSolver extends JSSEvalSolver {

	private static final int NUMBER_OF_RULES = 2;

	public CoopTwoRuleSolver() {
		super();
	}

	@Override
	protected void setChildConfiguration(JSSEvalConfiguration config) {
		if (config.getRuleNum() != NUMBER_OF_RULES) {
			throw new IllegalConfigurationException("The number of rules for the CoopTwoRuleSolver must be 2. Got " + config.getRuleNum());
		}

		CompletelyReactiveSolver solver = new CompletelyReactiveSolver();

		solver.setRule(new CoopTwoRuleDR(config.getRules()));

		setSolver(solver);
	}

}
