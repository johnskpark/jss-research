package jss;

import java.util.List;

/**
 * Representation of a job shop scheduling problem instance. IProblemInstance
 * stores all of the data that makes up a job shop scheduling problem
 * instance. In the most basic situation, this consists of the list of jobs,
 * machines and operations that need to be carried out on the machines. In
 * more complex situations, it includes future breakdowns, changes in job and
 * machine properties, etc.
 *
 * In addition to storing the properties of the problem instance,
 * IProblemInstance also stores the current state of the simulation as the
 * jobs are processed by the machines. The solver directly modifies this state
 * as it solves the problem. If the solver carries out an invalid action, an
 * Exception will be thrown by relevant component (either the machine or the
 * job). TODO elaborate on the Exception that is thrown.
 *
 * IProblemInstance should be used in conjunction with
 * @see ISubscriptionHandler to notify the @see ISolver as updates occur
 * during the simulation.
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
