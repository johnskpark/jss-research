package jss.evaluation;

import jss.IProblemInstance;
import jss.IResult;
import jss.ISolver;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public abstract class JSSEvalSolver implements ISolver {

	private ISolver solver;

	private int seed;

	private JSSEvalData data;

	/**
	 * TODO javadoc.
	 */
	public JSSEvalSolver() {
	}

	/**
	 * TODO javadoc.
	 * @param config
	 */
	public void setConfiguration(JSSEvalConfiguration config) {
		seed = config.getSeed();
		setChildConfiguration(config);
	}

	/**
	 * TODO javadoc.
	 * @param config
	 */
	protected abstract void setChildConfiguration(JSSEvalConfiguration config);

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

	/**
	 * TODO javadoc.
	 * @return
	 */
	public int getSeed() {
		return seed;
	}

	@Override
	public IResult getSolution(IProblemInstance problem)
			throws RuntimeException {
		return solver.getSolution(problem);
	}

}
