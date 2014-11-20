package jss.problem.dynamic_problem;

import java.util.ArrayList;
import java.util.List;

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
public class DynamicInstance implements IProblemInstance, ISubscriptionHandler {

	private IDoubleValueGenerator processingTimeGenerator;
	private IDoubleValueGenerator jobReadyTimeGenerator;
	private IDoubleValueGenerator dueDateGenerator;
	private IDoubleValueGenerator penaltyGenerator;

	private List<DynamicJob> jobs = new ArrayList<DynamicJob>();
	private List<IJob> incompleteJobs = new ArrayList<IJob>();

	private List<DynamicMachine> machines = new ArrayList<DynamicMachine>();

	private List<ISubscriber> subscribers = new ArrayList<ISubscriber>();

	/**
	 * TODO javadoc.
	 */
	public DynamicInstance() {
	}

	public void addMachine(DynamicMachine machine) {
		machines.add(machine);
	}

	/**
	 * TODO javadoc.
	 * @param ptg
	 */
	public void setProcessingTimeGenerator(IDoubleValueGenerator ptg) {
		processingTimeGenerator = ptg;
	}

	/**
	 * TODO javadoc.
	 * @param jrtg
	 */
	public void setJobReadyTimeGenerator(IDoubleValueGenerator jrtg) {
		jobReadyTimeGenerator = jrtg;
	}

	/**
	 * TODO javadoc.
	 * @param ddg
	 */
	public void setDueDateGenerator(IDoubleValueGenerator ddg) {
		dueDateGenerator = ddg;
	}

	/**
	 * TODO javadoc.
	 * @param pg
	 */
	public void setPenaltyGenerator(IDoubleValueGenerator pg) {
		penaltyGenerator = pg;
	}

	@Override
	public List<IJob> getJobs() {
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
		jobs = new ArrayList<DynamicJob>();
		incompleteJobs = new ArrayList<IJob>();

		for (DynamicMachine machine : machines) {
			machine.reset();
		}

		processingTimeGenerator.reset();
		jobReadyTimeGenerator.reset();
		dueDateGenerator.reset();
		penaltyGenerator.reset();

		subscribers = new ArrayList<ISubscriber>();
	}

	@Override
	public void initialise() {
		generateJob();

		for (DynamicJob job : jobs) {
			job.getNextMachine().addWaitingJob(job);
		}
	}

	private void generateJob() {
		// TODO hefty component where the job's properties are set.
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
