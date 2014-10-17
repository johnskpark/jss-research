package jss.evolution.sample;

import jss.evolution.JSSGPConfiguration;
import jss.evolution.JSSGPSolver;
import jss.problem.CompletelyReactiveSolver;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class PriorityBasedSolver extends JSSGPSolver {

	/**
	 * TODO javadoc.
	 */
	public PriorityBasedSolver() {
		super();
	}

	@Override
	public void setGPConfiguration(JSSGPConfiguration config) {
		CompletelyReactiveSolver solver = new CompletelyReactiveSolver();

		solver.setRule(new PriorityBasedDR(config.getState(),
				config.getIndividuals()[0],
				config.getThreadnum(),
				config.getData()));

		setSolver(solver);
	}
}
