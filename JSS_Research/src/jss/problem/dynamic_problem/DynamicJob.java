package jss.problem.dynamic_problem;

import jss.IEvent;
import jss.IEventHandler;
import jss.IJob;
import jss.IMachine;
import jss.IllegalActionException;

public class DynamicJob implements IJob, IEventHandler {

	@Override
	public double getReadyTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getProcessingTime(IMachine machine) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getSetupTime(IMachine machine) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getDueDate() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getPenalty() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getRemainingTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getRemainingOperation() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void startedProcessingOnMachine(IMachine machine)
			throws IllegalActionException {
		// TODO Auto-generated method stub

	}

	@Override
	public void finishProcessingOnMachine() {
		// TODO Auto-generated method stub

	}

	@Override
	public IMachine getCurrentMachine() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMachine getNextMachine() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMachine getLastMachine() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isProcessable(IMachine machine) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCompleted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reset() {
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
