package jss.evolution;

import jss.IProblemInstance;
import jss.IResult;
import jss.ISolver;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public abstract class JSSGPSolver implements ISolver {

	private ISolver solver;

	/**
	 * TODO javadoc.
	 */
	public JSSGPSolver() {
	}

	/**
	 * TODO javadoc.
	 * @param config
	 */
	public abstract void setGPConfiguration(JSSGPConfiguration config);

	/**
	 * TODO javadoc.
	 * @param solver
	 */
	protected void setSolver(ISolver solver) {
		this.solver = solver;
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	protected ISolver getSolver() {
		return solver;
	}

	@Override
	public IResult getSolution(IProblemInstance problem)
			throws RuntimeException {
		return solver.getSolution(problem);
	}
}
