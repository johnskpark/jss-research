package jss.heuristic;

import jss.problem.IJob;
import jss.problem.IMachine;

/**
 * In a job shop scheduling problem, an action from a solution generator
 * assigns the job to the particular machine. It is up to the solution
 * generator to ensure that the job to machine assignment is valid (i.e.
 * job can be processed at the machine, machine is not busy, etc.).
 *
 * @author parkjohn
 *
 */
public interface Action {

	/**
	 * Assign the job to the machine to be processed.
	 * @param job The job to be processed.
	 * @param machine The machine that processes the job.
	 * @param time The earliest time the job is set to be processed.
	 */
	public void assign(IJob job, IMachine machine, double time);
}
