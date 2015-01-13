package jss.problem.breakdown_problem;

import java.util.ArrayList;
import java.util.List;

import jss.IEventHandler;
import jss.IJob;
import jss.IMachine;
import jss.ISubscriber;
import jss.problem.BaseInstance;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class BreakdownInstance extends BaseInstance {

	private List<BreakdownJob> jobs = new ArrayList<BreakdownJob>();
	private List<BreakdownMachine> machines = new ArrayList<BreakdownMachine>();

	private IEventHandler breakdownHandler;

	private List<ISubscriber> subscribers = new ArrayList<ISubscriber>();

	/**
	 * TODO javadoc.
	 */
	public BreakdownInstance(IEventHandler handler) {
		breakdownHandler = handler;
	}

	public void addJob(BreakdownJob job) {
		jobs.add(job);
	}

	public void addMachine(BreakdownMachine machine) {
		machines.add(machine);
	}

	@Override
	public int getWarmUp() {
		return 0;
	}

	@Override
	public boolean isWarmUpComplete() {
		return true;
	}

	@Override
	public List<IEventHandler> getEventHandlers() {
		List<IEventHandler> eventHandlers = new ArrayList<IEventHandler>(jobs.size() + machines.size());

		eventHandlers.addAll(jobs);
		eventHandlers.addAll(machines);
		eventHandlers.add(breakdownHandler);

		return eventHandlers;
	}

	@Override
	public void reset() {
		for (BreakdownJob job : jobs) {
			job.reset();
		}

		for (BreakdownMachine machine : machines) {
			machine.reset();
		}

		subscribers = new ArrayList<ISubscriber>();
	}

	@Override
	public void initialise() {
		throw new UnsupportedOperationException("Not yet implemented");
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
