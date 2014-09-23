package jss.problem;

import java.util.List;

import jss.Event;
import jss.EventHandler;
import jss.IJob;
import jss.IMachine;
import jss.Subscriber;
import jss.SubscriptionHandler;

public abstract class GenericMachine implements IMachine, EventHandler, SubscriptionHandler {

	@Override
	public void onSubscriptionRequest(Subscriber s) {
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
	public Event getNextEvent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getNextEventTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IJob getCurrentJob() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IJob> getProcessedJobs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processJob(IJob job) throws RuntimeException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isAvailable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double getTimeAvailable() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

}
