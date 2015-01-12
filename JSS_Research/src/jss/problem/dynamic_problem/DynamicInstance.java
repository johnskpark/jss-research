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

	private ITerminationCriterion terminationCriterion;

	private List<DynamicJob> jobs = new ArrayList<DynamicJob>();
	private List<IJob> unreleasedJobs = new ArrayList<IJob>();
	private List<IJob> incompleteJobs = new ArrayList<IJob>();

	private List<DynamicMachine> machines = new ArrayList<DynamicMachine>();

	private List<ISubscriber> subscribers = new ArrayList<ISubscriber>();

	private int warmUp = 0;

	/**
	 * Generate a new dynamic job shop scheduling problem instance.
	 */
	public DynamicInstance() {
	}

	public void addMachine(DynamicMachine machine) {
		machines.add(machine);
	}

	/**
	 * Setter method for the generator that generate a list of machines to be
	 * processed on for the order of operations for a job.
	 */
	public void setProcessingOrderGenerator(IProcessingOrderGenerator pog) {
		processingOrderGenerator = pog;
	}

	/**
	 * Setter method for the generator that generate double values for the
	 * processing time of a job on machines.
	 */
	public void setProcessingTimeGenerator(IDoubleValueGenerator ptg) {
		processingTimeGenerator = ptg;
	}

	/**
	 * Setter method for the generator that generate double values for the
	 * job ready time of a job on machines.
	 */
	public void setJobReadyTimeGenerator(IDoubleValueGenerator jrtg) {
		jobReadyTimeGenerator = jrtg;
	}

	/**
	 * Setter method for the generator that generate double values for the
	 * due date time of a job.
	 */
	public void setDueDateGenerator(IDoubleValueGenerator ddg) {
		dueDateGenerator = ddg;
	}

	/**
	 * Setter method for the generator that generate double values for the
	 * penalty factor for a tardy job.
	 */
	public void setPenaltyGenerator(IDoubleValueGenerator pg) {
		penaltyGenerator = pg;
	}

	/**
	 * Setter method for the generator that generate double values for the
	 * setup time of a job on machines.
	 */
	public void setSetupTimeGenerator(IDoubleValueGenerator stg) {
		setupTimeGenerator = stg;
	}

	/**
	 * Setter method for the termination criterion that stop job generation
	 * when a termination criterion is reached.
	 */
	public void setTerminationCriterion(ITerminationCriterion tc) {
		terminationCriterion = tc;
	}

	/**
	 * TODO javadoc.
	 * @param warmUp
	 */
	public void setWarmUp(int warmUp) {
		this.warmUp = warmUp;
	}

	/**
	 * Returns the number of jobs completed in the simulation.
	 */
	public int getNumJobsCompleted() {
		return jobs.size() - incompleteJobs.size();
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
	public int getWarmUp() {
		return warmUp;
	}

	@Override
	public boolean isWarmUpComplete() {
		return getNumJobsCompleted() >= warmUp;
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

		unreleasedJobs = new ArrayList<IJob>();
		incompleteJobs = new ArrayList<IJob>();

		for (DynamicMachine machine : machines) {
			machine.reset();
		}

		processingOrderGenerator.reset();

		resetProcessingTimeGenerator();
		resetJobReadyTimeGenerator();
		resetDueDateGenerator();
		resetPenaltyGenerator();
		resetSetupTimeGenerator();

		subscribers = new ArrayList<ISubscriber>();
	}

	@Override
	public void initialise() {
		generateJob(0.0);
	}

	// Generate a new job using the generators.
	private void generateJob(double currentTime) {
		DynamicJob job = new DynamicJob(this);

		// Get the list of machines that the job needs to be processed on into.
		List<DynamicMachine> machineOrder = processingOrderGenerator.getProcessingOrder(machines);

		for (DynamicMachine machine : machineOrder) {
			job.offerMachine(machine);
			job.setProcessingTime(machine, generateProcessingTime(job));
			job.setSetupTime(machine, generateSetupTime(job));
		}

		job.setReadyTime(currentTime + generateJobReadyTime(job));
		job.setDueDate(generateDueDate(job));
		job.setPenalty(generatePenalty(job));

		unreleasedJobs.add(job);
		jobs.add(job);
	}

	// Generate values for the job.

	private double generateProcessingTime(DynamicJob job) {
		return (processingTimeGenerator != null) ? processingTimeGenerator.getDoubleValue(job) : 0;
	}

	private double generateSetupTime(DynamicJob job) {
		return (setupTimeGenerator != null) ? setupTimeGenerator.getDoubleValue(job) : 0;
	}

	private double generateJobReadyTime(DynamicJob job) {
		return (jobReadyTimeGenerator != null) ? jobReadyTimeGenerator.getDoubleValue(job) : 0;
	}

	private double generateDueDate(DynamicJob job) {
		return (dueDateGenerator != null) ? dueDateGenerator.getDoubleValue(job) : 0;
	}

	private double generatePenalty(DynamicJob job) {
		return (penaltyGenerator != null) ? penaltyGenerator.getDoubleValue(job) : 0;
	}

	// Reset the generators.

	private void resetProcessingTimeGenerator() {
		if (processingTimeGenerator != null) { processingTimeGenerator.reset(); }
	}

	private void resetSetupTimeGenerator() {
		if (setupTimeGenerator != null) { setupTimeGenerator.reset(); }
	}

	private void resetJobReadyTimeGenerator() {
		if (jobReadyTimeGenerator != null) { jobReadyTimeGenerator.reset(); }
	}

	private void resetDueDateGenerator() {
		if (dueDateGenerator != null) { dueDateGenerator.reset(); }
	}

	private void resetPenaltyGenerator() {
		if (penaltyGenerator != null) { penaltyGenerator.reset(); }
	}

	@Override
	public void onSubscriptionRequest(ISubscriber subscriber) {
		subscribers.add(subscriber);
	}

	@Override
	public void sendMachineFeed(IMachine machine, double time) {
		// Remove the job from the list of incomplete jobs.
		IJob job = machine.getLastProcessedJob();
		if (job != null) {
			incompleteJobs.remove(job);
		}

		for (ISubscriber subscriber : subscribers) {
			subscriber.onMachineFeed(machine, time);
		}
	}

	@Override
	public void sendJobFeed(IJob job, double time) {
		// Reveal the job to the market if the current time is past its ready time.
		if (time >= job.getReadyTime()) {
			incompleteJobs.add(job);
			unreleasedJobs.remove(job);

			job.getCurrentMachine().addWaitingJob(job);
		}

		// Generate new jobs if the termination criterion has not yet been reached.
		if (!terminationCriterion.criterionMet()) {
			generateJob(time);
		}

		for (ISubscriber subscriber : subscribers) {
			subscriber.onJobFeed(job, time);
		}
	}

}
