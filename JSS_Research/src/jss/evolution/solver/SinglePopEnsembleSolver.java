package jss.evolution.solver;

import jss.evolution.JSSGPConfiguration;
import jss.evolution.JSSGPSolver;
import jss.problem.CompletelyReactiveSolver;

public class SinglePopEnsembleSolver extends JSSGPSolver {

	public SinglePopEnsembleSolver() {
		super();
	}

	@Override
	public void setGPConfiguration(JSSGPConfiguration config) {
		CompletelyReactiveSolver solver = new CompletelyReactiveSolver();

		solver.setRule(new SinglePopEnsembleDR(config.getState(),
				config.getIndividuals(),
				config.getThreadnum(),
				config.getData(),
				config.getTracker()));

		setSolver(solver);
	}

}
