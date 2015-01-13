package jss;

import java.util.List;
import java.util.Set;

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
 * @see IllegalActionException will be thrown by relevant component (either
 * the machine or the job).
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
	public List<? extends IJob> getJobs();

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
	public Set<? extends IMachine> getMachines();

	/**
	 * TODO javadoc.
	 * @return
	 */
	public int getNumJobs();

	/**
	 * TODO javadoc.
	 * @return
	 */
	public int getNumMachines();

	/**
	 * TODO javadoc.
	 * @return
	 */
	public Set<? extends IMachine> getAvailableMachines();

	/**
	 * TODO javadoc.
	 * @return
	 */
	public int getWarmUp();

	/**
	 * TODO javadoc.
	 * @return
	 */
	public boolean isWarmUpComplete();

	/**
	 * Get the list of event handlers that store events to be used by the
	 * Simulator visible at the current state of the problem.
	 * @return Get a fresh list of references to event handlers that are
	 *         visible in the problem instance at the current state.
	 */
	public List<? extends IEventHandler> getEventHandlers();

	/**
	 * Reset any modifications made to the internal state of the problem
	 * instance.
	 */
	public void reset();

	/**
	 * TODO javadoc.
	 */
	public void initialise();

}
