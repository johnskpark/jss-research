package jss.evolution.sample;

import java.util.ArrayList;
import java.util.List;

import jss.problem.IJob;
import jss.problem.IMachine;

public class BasicMachine implements IMachine {

	private List<IJob> prevJobs = new ArrayList<IJob>();

	private BasicJob currentJob = null;
	private double availableTime = 0;

	public BasicMachine() {
	}

	@Override
	public IJob getCurrentJob() {
		return currentJob;
	}

	@Override
	public List<IJob> getProcessedJobs() {
		return prevJobs;
	}

	@Override
	public void processJob(IJob job) throws RuntimeException {
		if (currentJob != null) {
			throw new RuntimeException("You done goofed from BasicMachine");
		}

		currentJob = (BasicJob)job;
		availableTime = Math.max(availableTime, job.getReleaseTime()) +
				job.getSetupTime(this) +
				job.getProcessingTime(this);
	}

	@Override
	public boolean isAvailable() {
		return currentJob == null;
	}

	@Override
	public double getTimeAvailable() {
		return availableTime;
	}

	@Override
	public void clear() {
		prevJobs = new ArrayList<IJob>();

		currentJob = null;
		availableTime = 0;
	}

}
