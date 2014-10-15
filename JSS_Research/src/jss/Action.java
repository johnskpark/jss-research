package jss;


/**
 * In a Job Shop Scheduling problem, an action from a solution generator
 * assigns the job to the particular machine. It is up to the solution
 * generator to ensure that the job to machine assignment is valid (i.e.
 * job can be processed at the machine, machine is not busy, etc.).
 *
 * @author parkjohn
 *
 */
public class Action {

	private IMachine machine;
	private IJob job;
	private double time;

	/**
	 * Generate a new instance of an action.
	 * @param machine
	 * @param job
	 * @param time
	 */
	public Action(IMachine machine, IJob job, double time) {
		this.machine = machine;
		this.job = job;
		this.time = time;
	}

	/**
	 * Get the job to process at the machine.
	 * @return
	 */
	public IJob getJob() {
		return job;
	}

	/**
	 * Get the machine that will be processing the job.
	 * @return
	 */
	public IMachine getMachine() {
		return machine;
	}

	/**
	 * Get the time when the processing should start.
	 * @return
	 */
	public double getTime() {
		return time;
	}
}
