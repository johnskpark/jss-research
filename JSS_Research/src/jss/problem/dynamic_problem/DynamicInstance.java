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

	private IProcessingOrderGenerator processingOrderGenerator;

	private IDoubleValueGenerator processingTimeGenerator;
	private IDoubleValueGenerator jobReadyTimeGenerator;
	private IDoubleValueGenerator dueDateGenerator;
	private IDoubleValueGenerator penaltyGenerator;
	private IDoubleValueGenerator setupTimeGenerator;

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
	 * @param pog
	 */
	public void setProcessingOrderGenerator(IProcessingOrderGenerator pog) {
		processingOrderGenerator = pog;
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

	/**
	 * TODO javadoc.
	 * @param stg
	 */
	public void setSetupTimeGenerator(IDoubleValueGenerator stg) {
		setupTimeGenerator = stg;
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

		processingOrderGenerator.reset();

		processingTimeGenerator.reset();
		jobReadyTimeGenerator.reset();
		dueDateGenerator.reset();
		penaltyGenerator.reset();
		setupTimeGenerator.reset();

		subscribers = new ArrayList<ISubscriber>();
	}

	@Override
	public void initialise() {
		generateJob();

		for (DynamicJob job : jobs) {
			job.getNextMachine().addWaitingJob(job);
		}
	}

	// Generate a new job using the generators.
	private void generateJob() {
		DynamicJob job = new DynamicJob();

		// Get the list of machines that the job needs to be processed on into.
		List<DynamicMachine> machineOrder = processingOrderGenerator.getProcessingOrder(machines);

		for (DynamicMachine machine : machineOrder) {
			job.offerMachine(machine);
			job.setProcessingTime(machine, generateProcessingTime(job));
			job.setSetupTime(machine, generateSetupTime(job));
		}

		job.setReadyTime(generateJobReadyTime(job));
		job.setDueDate(generateDueDate(job));
		job.setPenalty(generatePenalty(job));

		jobs.add(job);
		// TODO need to add to the current time. I also need a stopping criterion.
	}

	// Generate processing time for the job.
	private double generateProcessingTime(DynamicJob job) {
		return (processingTimeGenerator != null) ? processingTimeGenerator.getDoubleValue(job) : 0;
	}

	// Generate setup time for the job.
	private double generateSetupTime(DynamicJob job) {
		return (setupTimeGenerator != null) ? setupTimeGenerator.getDoubleValue(job) : 0;
	}

	// Generate job ready time for the job.
	private double generateJobReadyTime(DynamicJob job) {
		return (jobReadyTimeGenerator != null) ? jobReadyTimeGenerator.getDoubleValue(job) : 0;
	}

	// Generate due date time for the job.
	private double generateDueDate(DynamicJob job) {
		return (dueDateGenerator != null) ? dueDateGenerator.getDoubleValue(job) : 0;
	}

	// Generate penalty for tardy job.
	private double generatePenalty(DynamicJob job) {
		return (penaltyGenerator != null) ? penaltyGenerator.getDoubleValue(job) : 0;
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
