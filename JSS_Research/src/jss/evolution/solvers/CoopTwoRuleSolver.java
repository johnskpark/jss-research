package jss.evolution.solvers;

import jss.evolution.JSSGPConfiguration;
import jss.evolution.JSSGPSolver;
import jss.problem.CompletelyReactiveSolver;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class CoopTwoRuleSolver extends JSSGPSolver {

	/**
	 * TODO javadoc.
	 */
	public CoopTwoRuleSolver() {
		super();
	}

	@Override
	public void setGPConfiguration(JSSGPConfiguration config) {
		CompletelyReactiveSolver solver = new CompletelyReactiveSolver();

		solver.setRule(new CoopTwoRuleDR(config.getState(),
				config.getIndividuals(),
				config.getThreadnum(),
				config.getData()));

		setSolver(solver);
	}

}
