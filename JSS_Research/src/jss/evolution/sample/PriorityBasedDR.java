package jss.evolution.sample;

import jss.evolution.JSSGPConfiguration;
import jss.evolution.JSSGPRule;
import jss.evolution.JSSGPSolver;
import jss.problem.CompletelyReactiveSolver;

public class PriorityBasedDR extends JSSGPSolver {

	public PriorityBasedDR() {
		super();
	}

	@Override
	public void setGPConfiguration(JSSGPConfiguration config) {
		CompletelyReactiveSolver solver = new CompletelyReactiveSolver();

		solver.setRule(new JSSGPRule(config.getState(),
				config.getIndividual(),
				config.getThreadnum(),
				config.getData()));

		setSolver(solver);
	}
}
