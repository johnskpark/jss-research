package jss.evolution;

import jss.IJob;
import jss.IMachine;
import jss.IProblemInstance;
import ec.gp.GPData;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class JSSData extends GPData {

	private static final long serialVersionUID = 5L;

	private IProblemInstance problem;
	private IMachine machine;
	private IJob job;

	private double priority;

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

	/**
	 * TODO javadoc.
	 * @param problem
	 */
	public void setProblem(IProblemInstance problem) {
		this.problem = problem;
	}

	/**
	 * TODO javadoc.
	 * @param machine
	 */
	public void setMachine(IMachine machine) {
		this.machine = machine;
	}

	/**
	 * TODO javadoc.
	 * @param job
	 */
	public void setJob(IJob job) {
		this.job = job;
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	public double getPriority() {
		return priority;
	}

	/**
	 * TODO javadoc.
	 * @param priority
	 */
	public void setPriority(double priority) {
		this.priority = priority;
	}

	/**
	 * TODO javadoc.
	 */
	public void clear() {
		machine = null;
		job = null;
	}
}
