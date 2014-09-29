package jss.problem.static_problem;

import java.util.ArrayList;
import java.util.List;

import jss.IEventHandler;
import jss.IJob;
import jss.IMachine;
import jss.IProblemInstance;

public class StaticInstanceBuilder {

	private List<StaticJob> jobs = new ArrayList<StaticJob>();
	private List<StaticMachine> machines = new ArrayList<StaticMachine>();

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
		return new StaticInstance(jobs, machines);
	}

	/**
	 * TODO javadoc.
	 */
	public void reset() {
		jobs = new ArrayList<StaticJob>();
		machines = new ArrayList<StaticMachine>();
	}

	// Inner unmodifiable class. TODO more documentation.
	private class StaticInstance implements IProblemInstance {

		private List<StaticJob> jobs;
		private List<StaticMachine> machines;

		public StaticInstance(List<StaticJob> jobs, List<StaticMachine> machines) {
			this.jobs = jobs;
			this.machines = machines;
		}

		@Override
		public List<IJob> getJobs() {
			List<IJob> incompleteJobs = new ArrayList<IJob>();

			for (IJob job : jobs) {
				if (!job.isCompleted()) {
					incompleteJobs.add(job);
				}
			}

			return incompleteJobs;
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
			for (StaticJob job : jobs) {
				job.reset();
			}

			for (StaticMachine machine : machines) {
				machine.reset();
			}
		}

	}

}
