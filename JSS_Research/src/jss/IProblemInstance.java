package jss;

import java.util.List;

/**
 * TODO problem instance that covers the machines and the jobs.
 *
 * @author parkjohn
 *
 */
public interface IProblemInstance {

	/**
	 * Get the list of jobs that are visible in the problem.
	 *
	 * TODO elaborate on the visibility.
	 * @return
	 */
	public List<IJob> getJobs();

	/**
	 * Get the list of machines that are visible in the problem.
	 *
	 * TODO elaborate on the visibility.
	 * @return
	 */
	public List<IMachine> getMachines();

	/**
	 * TODO
	 * @return
	 */
	public List<EventHandler> getEventHandlers();

	/**
	 * TODO
	 */
	public void reset();
}
