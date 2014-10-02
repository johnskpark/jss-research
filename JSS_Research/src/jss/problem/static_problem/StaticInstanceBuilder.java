package jss.problem.static_problem;

import java.util.ArrayList;
import java.util.List;

import jss.IEventHandler;
import jss.IJob;
import jss.IMachine;
import jss.IProblemInstance;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class StaticInstanceBuilder {

	private StaticInstance problem = new StaticInstance();

	private List<StaticJob> jobs = new ArrayList<StaticJob>();
	private List<StaticMachine> machines = new ArrayList<StaticMachine>();

	/**
	 * TODO javadoc.
	 */
	public StaticInstanceBuilder() {
	}

	/**
	 * Add an instance of a BasicJob into the problem instance.
	 * @param job The job to add to the problem instance.
	 */
	public void addJob(StaticJob job) {
		jobs.add(job);
	}

	/**
	 * Add an instance of a BasicMachine into the problem instance.
	 * @param machine The machine to add to the problem instance.
	 */
	public void addMachine(StaticMachine machine) {
		machines.add(machine);
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	public IProblemInstance createNewInstance() {
		return problem;
	}

	/**
	 * TODO javadoc.
	 */
	public void reset() {
		jobs = new ArrayList<StaticJob>();
		machines = new ArrayList<StaticMachine>();
	}

}
