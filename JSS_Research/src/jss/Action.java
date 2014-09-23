package jss;


/**
 * In a job shop scheduling problem, an action from a solution generator
 * assigns the job to the particular machine. It is up to the solution
 * generator to ensure that the job to machine assignment is valid (i.e.
 * job can be processed at the machine, machine is not busy, etc.).
 *
 * TODO I'm not even sure if we need this
 *
 * @author parkjohn
 *
 */
public class Action {

	private IMachine machine;
	private IJob job;
	private double time;

	public Action(IMachine machine, IJob job, double time) {
		this.machine = machine;
		this.job = job;
		this.time = time;
	}

	public IJob getJob() {
		return job;
	}

	public IMachine getMachine() {
		return machine;
	}

	public double getTime() {
		return time;
	}
}
