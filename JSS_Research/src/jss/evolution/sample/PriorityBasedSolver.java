package jss.evolution.sample;

import jss.evolution.JSSGPConfiguration;
import jss.evolution.JSSGPRule;
import jss.evolution.JSSGPSolver;
import jss.problem.CompletelyReactiveSolver;

public class PriorityBasedSolver extends JSSGPSolver {

	public PriorityBasedSolver() {
		super();
	}

	@Override
	public void setGPConfiguration(JSSGPConfiguration config) {
		CompletelyReactiveSolver solver = new CompletelyReactiveSolver();

		solver.setRule(new PriorityBasedDR(config.getState(),
				config.getIndividual(),
				config.getThreadnum(),
				config.getData()));

		setSolver(solver);
	}
}
