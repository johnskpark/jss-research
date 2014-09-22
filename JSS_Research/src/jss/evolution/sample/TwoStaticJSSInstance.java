package jss.evolution.sample;

import java.util.ArrayList;
import java.util.List;

import jss.problem.IJob;
import jss.problem.IMachine;
import jss.problem.IProblemInstance;

/**
 * Really, really basic problem instance.
 * @author parkjohn
 *
 */
public class TwoStaticJSSInstance implements IProblemInstance {

	private BasicMachine machine1;
	private BasicMachine machine2;

	private List<BasicJob> jobs = new ArrayList<BasicJob>();

	public TwoStaticJSSInstance() {
	}

	public BasicMachine getMachine1() {
		return machine1;
	}

	public BasicMachine getMachine2() {
		return machine2;
	}

	public void addJob(BasicJob job) {
		jobs.add(job);
	}

	public List<BasicJob> getJobs() {
		return jobs;
	}
}
