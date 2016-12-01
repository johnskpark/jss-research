package app.jasimaShopSim.core;

import java.util.Set;

import app.jasimaShopSim.core.IdleTime.IdleTimeEvent;
import jasima.core.util.ValueStore;
import jasima.core.util.observer.Notifier;
import jasima.core.util.observer.NotifierAdapter;
import jasima.core.util.observer.NotifierListener;
import jasima.shopSim.core.Job;
import jasima.shopSim.core.JobShop;
import jasima.shopSim.core.Operation;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;

// TODO one issue I have with this is how do we know how long to idle for?

public class IdleTime extends PrioRuleTarget implements Cloneable,
		Notifier<IdleTime, IdleTimeEvent>, ValueStore {

	/** Base class for idle time events */
	public static class IdleTimeEvent {
	}

	public static final IdleTimeEvent IDLE_TIME_INTRODUCED = new IdleTimeEvent();
	public static final IdleTimeEvent IDLE_TIME_STARTED = new IdleTimeEvent();
	public static final IdleTimeEvent IDLE_TIME_FINISHED = new IdleTimeEvent();

	private final JobShop shop;

	private double machineReadyTime;
	private WorkStation currMachine;
	private double startTime;
	private double relDate;
	private double weight = 0.0d; // idle time should have no weight.
	private Operation[] ops; // TODO

	// TODO the variables go here.

	public IdleTime(JobShop shop) {
		super();
		this.shop = shop;
	}

	@Override
	public boolean isFuture() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Operation getCurrentOperation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WorkStation getCurrMachine() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JobShop getShop() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTaskNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int numOps() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Operation[] getOps() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getArriveTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double remainingProcTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getDueDate() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getWeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double currProcTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getRelDate() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getJobNum() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getCurrentOperationDueDate() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double procSum() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int numOpsLeft() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int numJobsInBatch() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Job job(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isBatch() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int valueStoreGetNumKeys() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Set<Object> valueStoreGetAllKeys() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object valueStoreRemove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	private NotifierAdapter<IdleTime, IdleTimeEvent> adapter = null;

	@Override
	public void addNotifierListener(NotifierListener<IdleTime, IdleTimeEvent> listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public NotifierListener<IdleTime, IdleTimeEvent> getNotifierListener(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeNotifierListener(NotifierListener<IdleTime, IdleTimeEvent> listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public int numListener() {
		// TODO Auto-generated method stub
		return 0;
	}

	protected void fire(IdleTimeEvent event) {
		if (adapter != null) {
			adapter.fire(event);
		}
	}

}
