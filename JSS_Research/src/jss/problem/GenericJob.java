package jss.problem;

import jss.IEvent;
import jss.IEventHandler;
import jss.IJob;
import jss.IMachine;
import jss.ISubscriber;
import jss.ISubscriptionHandler;

public abstract class GenericJob implements IJob, IEventHandler, ISubscriptionHandler {

	@Override
	public void onSubscriptionRequest(ISubscriber s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendMachineFeed(IMachine machine) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendJobFeed(IJob job) {
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

	@Override
	public double getReleaseTime() {
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
	public double getDueDate(IMachine machine) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void visitMachine(IMachine machine) throws RuntimeException {
		// TODO Auto-generated method stub

	}

	@Override
	public IMachine getNextMachine() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isProcessable(IMachine machine) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

}
