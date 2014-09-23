package jss.problem;

import java.util.ArrayList;
import java.util.List;

import jss.EventHandler;
import jss.IProblemInstance;

public abstract class GenericProblemInstance implements IProblemInstance {

	// TODO temporary protected. come up with better solution later.
	protected List<GenericJob> jobs = new ArrayList<GenericJob>();
	protected List<GenericMachine> machines = new ArrayList<GenericMachine>();

	@Override
	public List<EventHandler> getEventHandlers() {
		List<EventHandler> eventHandlers = new ArrayList<EventHandler>(jobs.size() + machines.size());

		eventHandlers.addAll(jobs);
		eventHandlers.addAll(machines);

		return eventHandlers;
	}

	@Override
	public void reset() {
		for (GenericJob job : jobs) {
			job.reset();
		}

		for (GenericMachine machine : machines) {
			machine.reset();
		}
	}
}
