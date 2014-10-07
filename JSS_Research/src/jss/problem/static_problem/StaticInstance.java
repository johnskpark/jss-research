package jss.problem.static_problem;

import java.util.ArrayList;
import java.util.List;

import jss.IEventHandler;
import jss.IJob;
import jss.IMachine;
import jss.IProblemInstance;
import jss.ISubscriber;
import jss.ISubscriptionHandler;

/**
 * Really, really basic problem instance. TODO write more.
 *
 * @author parkjohn
 *
 */
public class StaticInstance implements IProblemInstance, ISubscriptionHandler {

	private List<StaticJob> jobs = new ArrayList<StaticJob>();
	private List<StaticMachine> machines = new ArrayList<StaticMachine>();

	private List<ISubscriber> subscribers = new ArrayList<ISubscriber>();

	/**
	 * TODO javadoc.
	 */
	public StaticInstance() {
	}

	public void addJob(StaticJob job) {
		jobs.add(job);
	}

	public void addMachine(StaticMachine machine) {
		machines.add(machine);
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

		subscribers = new ArrayList<ISubscriber>();
	}

	@Override
	public void onSubscriptionRequest(ISubscriber subscriber) {
		subscribers.add(subscriber);
	}

	@Override
	public void sendMachineFeed(IMachine machine, double time) {
		for (ISubscriber subscriber : subscribers) {
			subscriber.onMachineFeed(machine, time);
		}
	}

	@Override
	public void sendJobFeed(IJob job, double time) {
		for (ISubscriber subscriber : subscribers) {
			subscriber.onJobFeed(job, time);
		}
	}
}
