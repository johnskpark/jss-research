package jss.evolution.sample;

import java.util.ArrayList;
import java.util.Arrays;
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

	private List<IJob> jobs = new ArrayList<IJob>();
	private List<IMachine> machines = Arrays.asList(new IMachine[]{new BasicMachine(), new BasicMachine()});

	public TwoStaticJSSInstance() {
	}

	public void addJob(BasicJob job) {
		jobs.add(job);
	}

	public List<IJob> getJobs() {
		return jobs;
	}

	public List<IMachine> getMachines() {
		return machines;
	}
}
