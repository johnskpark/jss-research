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

	private Set<BaseMachine> machines = new TreeSet<BaseMachine>();
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
	public List<? extends IJob> getJobs()	{
		return jobs;
	}

	@Override
	public Set<? extends IMachine> getMachines() {
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
	public Set<? extends IMachine> getAvailableMachines() {
		return availableMachines;
	}

	@Override
	public List<? extends IEventHandler> getEventHandlers() {
		return eventHandlers;
	}

	@Override
	public void reset() {
		eventHandlers.clear();
		
		eventHandlers.addAll(jobs);
		eventHandlers.addAll(machines);
		
		subscribers.clear();
	}
	
	@Override
	public void initialise() {
	}

	/// ISubscriptionHandler

	@Override
	public void onSubscriptionRequest(ISubscriber subscriber) {
		subscribers.add(subscriber);
	}

	private List<IMachine> unavailableMachines = new ArrayList<IMachine>();
	
	public List<IMachine> getUnavailableMachines() {
		return unavailableMachines;
	}

	@Override
	public void sendMachineFeed(IMachine machine, double time) {
		availableMachines.add(machine);
		
		for (ISubscriber subscriber : subscribers) {
			subscriber.onMachineFeed(machine, time);
		}

		updateBusyMachines();
	}

	@Override
	public void sendJobFeed(IJob job, double time) {
		for (ISubscriber subscriber : subscribers) {
			subscriber.onJobFeed(job, time);
		}
		
		updateBusyMachines();
	}
	
	private void updateBusyMachines() {
		availableMachines.removeAll(unavailableMachines);
		
		unavailableMachines.clear();
	}

}
