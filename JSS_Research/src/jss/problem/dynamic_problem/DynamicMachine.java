package jss.problem.dynamic_problem;

import java.util.List;

import jss.IEvent;
import jss.IEventHandler;
import jss.IJob;
import jss.IMachine;
import jss.IllegalActionException;

public class DynamicMachine implements IMachine, IEventHandler {

	@Override
	public IJob getCurrentJob() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IJob getLastProcessedJob() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IJob> getProcessedJobs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processJob(IJob job, double time) throws IllegalActionException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isAvailable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double getReadyTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void updateStatus(double time) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<IJob> getWaitingJobs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addWaitingJob(IJob job) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasEvent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IEvent getNextEvent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getNextEventTime() {
		// TODO Auto-generated method stub
		return 0;
	}

}
