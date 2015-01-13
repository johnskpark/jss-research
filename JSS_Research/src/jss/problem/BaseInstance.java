package jss.problem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import jss.IEventHandler;
import jss.IJob;
import jss.IMachine;
import jss.IProblemInstance;
import jss.ISubscriber;
import jss.ISubscriptionHandler;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public abstract class BaseInstance implements IProblemInstance, ISubscriptionHandler {

	private List<BaseJob> jobs = new ArrayList<BaseJob>();

	private Set<IMachine> machines = new TreeSet<IMachine>();
	private Set<IMachine> availableMachines = new TreeSet<IMachine>();

	private List<IEventHandler> eventHandlers = new ArrayList<IEventHandler>();

	private List<ISubscriber> subscribers = new ArrayList<ISubscriber>();

	// TODO the event handlers

	/**
	 * Generate a new dynamic job shop scheduling problem instance.
	 */
	public BaseInstance() {
	}

	/**
	 * TODO javadoc.
	 * @param job
	 */
	public void addJob(BaseJob job) {
		jobs.add(job);

		eventHandlers.add(job);
	}

	/**
	 * TODO javadoc.
	 * @param machine
	 */
	public void addMachine(BaseMachine machine) {
		machines.add(machine);
		availableMachines.add(machine);

		eventHandlers.add(machine);
	}

	/// IProblemInstance

	@Override
	public List<IJob> getJobs()	{
		return new ArrayList<IJob>(jobs);
	}

	@Override
	public Set<IMachine> getMachines() {
		return machines;
	}

	@Override
	public int getNumJobs() {
		return jobs.size();
	}

	@Override
	public int getNumMachines() {
		return machines.size();
	}

	@Override
	public Set<IMachine> getAvailableMachines() {
		return availableMachines;
	}

	@Override
	public List<IEventHandler> getEventHandlers() {
		return eventHandlers;
	}

	@Override
	public void reset() {
		jobs = new ArrayList<BaseJob>();

		for (IMachine machine : machines) {
			machine.reset();
		}

		subscribers = new ArrayList<ISubscriber>();
	}

	@Override
	public void initialise() {
	}

	/// ISubscriptionHandler

	@Override
	public void onSubscriptionRequest(ISubscriber subscriber) {
		subscribers.add(subscriber);
	}

	// TODO fuck, God I'm so goddamn fried in the head.
	public List<IMachine> unavailableMachines = new ArrayList<IMachine>();

	@Override
	public void sendMachineFeed(IMachine machine, double time) {
		for (ISubscriber subscriber : subscribers) {
			subscriber.onMachineFeed(machine, time);
		}

		// TODO this doesn't work.
		if (machine.isAvailable()) {
			availableMachines.add(machine);
		} else {
			availableMachines.remove(machine);
		}
	}

	@Override
	public void sendJobFeed(IJob job, double time) {
		for (ISubscriber subscriber : subscribers) {
			subscriber.onJobFeed(job, time);
		}
	}

}
