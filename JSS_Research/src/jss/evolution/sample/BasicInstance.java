package jss.evolution.sample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jss.IEventHandler;
import jss.IJob;
import jss.IMachine;
import jss.IProblemInstance;

/**
 * Really, really basic problem instance.
 * @author parkjohn
 *
 */
public class BasicInstance implements IProblemInstance {

	private List<BasicJob> jobs = new ArrayList<BasicJob>();
	private List<BasicMachine> machines = Arrays.asList(new BasicMachine[]{new BasicMachine(), new BasicMachine()});

	/**
	 * TODO javadoc.
	 */
	public BasicInstance() {
	}

	/**
	 * TODO javadoc.
	 * @param job
	 */
	public void addJob(BasicJob job) {
		jobs.add(job);
	}

	@Override
	public List<IJob> getJobs() {
		return new ArrayList<IJob>(jobs);
	}

	@Override
	public List<IMachine> getMachines() {
		return new ArrayList<IMachine>(machines);
	}

	@Override
	public List<IEventHandler> getEventHandlers() {
		List<IEventHandler> eventHandlers = new ArrayList<IEventHandler>(jobs.size() + machines.size());

		eventHandlers.addAll(jobs);
		eventHandlers.addAll(machines);

		return eventHandlers;
	}

	@Override
	public void reset() {
		for (BasicJob job : jobs) {
			job.reset();
		}

		for (BasicMachine machine : machines) {
			machine.reset();
		}
	}
}
