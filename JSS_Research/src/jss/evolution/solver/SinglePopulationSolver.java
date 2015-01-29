package jss.evolution.solver;

import jss.evolution.JSSGPConfiguration;
import jss.evolution.JSSGPSolver;
import jss.problem.CompletelyReactiveSolver;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class SinglePopulationSolver extends JSSGPSolver {

	/**
	 * TODO javadoc.
	 */
	public SinglePopulationSolver() {
		super();
	}

	@Override
	public void setGPConfiguration(JSSGPConfiguration config) {
		CompletelyReactiveSolver solver = new CompletelyReactiveSolver();

		solver.setRule(new SinglePopulationDR(config.getState(),
				config.getIndividuals(),
				config.getThreadnum(),
				config.getData(),
				config.getTracker()));

		setSolver(solver);
	}

}
