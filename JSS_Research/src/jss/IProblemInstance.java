package jss;

import java.util.List;

/**
 * TODO problem instance that covers the machines and the jobs. elaborate
 *
 * Modified by getting the jobs and machines and directly modifying them.
 *
 * @author parkjohn
 *
 */
public interface IProblemInstance {

	/**
	 * Get the list of job references that are visible in the problem, and is
	 * incomplete.
	 *
	 * The jobs that are returned by this method call are references to the
	 * jobs held by the problem instance. Therefore, any modifications made to
	 * the jobs externally will affect the jobs held internally in the problem
	 * instance. However, modifications to the list will not affect the
	 * internal state of the problem instance.
	 * @return Get a fresh list of references to jobs that are visible in the
	 *         problem instance at the current state, and are incomplete.
	 */
	public List<IJob> getJobs();

	/**
	 * Get the list of machines that are visible in the problem.
	 *
	 * The machines that are returned by this method call are references to
	 * the machines held by the problem instance. Therefore, any modification
	 * made to the machines externally will affect the machine held internally
	 * in the problem instance. However, modifications to the list will not
	 * affect the internal state of the problem instance.
	 * @return Get a fresh list of references to machines that are visible in
	 *         the problem instance at the current state.
	 */
	public List<IMachine> getMachines();

	/**
	 * Get the list of event handlers that store events to be used by the
	 * Simulator visible at the current state of the problem.
	 * @return Get a fresh list of references to event handlers that are
	 *         visible in the problem instance at the current state.
	 */
	public List<IEventHandler> getEventHandlers();

	/**
	 * Reset any modifications made to the internal state of the problem
	 * instance.
	 */
	public void reset();
}
