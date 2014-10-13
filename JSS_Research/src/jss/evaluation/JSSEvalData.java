package jss.evaluation;

import jss.IJob;
import jss.IMachine;
import jss.IProblemInstance;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class JSSEvalData {

	private IProblemInstance problem;
	private IMachine machine;
	private IJob job;

	public JSSEvalData(IProblemInstance problem,
			IMachine machine,
			IJob job) {
		this.problem = problem;
		this.machine = machine;
		this.job = job;
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	public IProblemInstance getProblem() {
		return problem;
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	public IMachine getMachine() {
		return machine;
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	public IJob getJob() {
		return job;
	}

}
