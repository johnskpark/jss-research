package jss.evolution.solvers;

import jss.evolution.JSSGPConfiguration;
import jss.evolution.JSSGPSolver;
import jss.problem.CompletelyReactiveSolver;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class CoopEnsembleSolver extends JSSGPSolver {

	/**
	 * TODO javadoc.
	 */
	public CoopEnsembleSolver() {
		super();
	}

	@Override
	public void setGPConfiguration(JSSGPConfiguration config) {
		CompletelyReactiveSolver solver = new CompletelyReactiveSolver();

		solver.setRule(new CoopEnsembleDR(config.getState(),
				config.getIndividuals(),
				config.getThreadnum(),
				config.getData(),
				config.getTracker()));

		setSolver(solver);
	}
}
