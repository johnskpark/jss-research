package jasima.shopSim.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

public class IdleTime extends Job {

	public static final JobEvent IDLE_TIME_INTRODUCED = new JobEvent();
	public static final JobEvent IDLE_TIME_STARTED = new JobEvent();
	public static final JobEvent IDLE_TIME_FINISHED = new JobEvent();

	private final double DUE_DATE = Double.POSITIVE_INFINITY; // idle time should not have a due date
	private final double WEIGHT = 0.0d; // idle time should have no weight.

	private double arriveTime; // should be at machine ready time.
	private WorkStation currMachine;
	private double startTime;
	private double finishTime;
	private double relDate;
	private int idleTimeNum; // global number of idle time in system.
	private Operation[] ops; // an idle time only has one operation.
	private String name = null;

	public IdleTime(JobShop shop) {
		super(shop);
	}

	public void setArriveTime(double arriveTime) {
		// The idle time's arrival should start at machine ready time.
		this.arriveTime = arriveTime;
	}

	@Override
	public double getArriveTime() {
		// The idle time is always inserted from the current time.
		return arriveTime;
	}

	@Override
	public int getTaskNumber() {
		return 0;
	}

	public void setCurrMachine(WorkStation currMachine) {
		this.currMachine = currMachine;
	}

	@Override
	public WorkStation getCurrMachine() {
		return currMachine;
	}

	@Override
	public Operation getCurrentOperation() {
		return ops[getTaskNumber()];
	}

	@Override
	public double currProcTime() {
		return ops[getTaskNumber()].procTime;
	}

	@Override
	public double procSum() {
		return currProcTime();
	}

	@Override
	public double remainingProcTime() {
		return currProcTime();
	}

	@Override
	public int numOps() {
		return getOps().length;
	}

	@Override
	public int numOpsLeft() {
		return getOps().length - getTaskNumber();
	}

	void proceed() {
		// Idle time isn't a job, so just ignore this.
	}

	void jobReleased() {
		// FIXME should be the same as idle time introduced (i.e. arrive in queue?)?
		// Let's leave it blank for now, and see what happens later down the line.
	}

	void arriveInQueue(WorkStation workStation, double arrivesAt) {
		setCurrMachine(workStation);
		setArriveTime(arrivesAt);

		fire(IDLE_TIME_INTRODUCED);
	}

	void removedFromQueue() {
		// Remove from queue only sends message to the listeners,
		// so don't bother doing anything.
	}

	void startProcessing() {
		setFinishTime(currMachine.currMachine.procFinished);
		setStartTime(currMachine.shop().simTime());

		fire(IDLE_TIME_STARTED);
	}

	void endProcessing() {
		fire(IDLE_TIME_FINISHED);
	}

	@Override
	public boolean isFuture() {
		return false;
	}

	@Override
	public String toString() {
		return getName();
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		if (name == null) {
			return getClass().getSimpleName() + "." + getJobNum();
		} else {
			return name;
		}
	}

	@Override
	public double getDueDate() {
		return DUE_DATE;
	}

	@Override
	public double getWeight() {
		return WEIGHT;
	}

	public void setRelDate(double relDate) {
		this.relDate = relDate;
	}

	@Override
	public double getRelDate() {
		return relDate;
	}

	public void setJobNum(int jobNum) {
		this.idleTimeNum = jobNum;
	}

	@Override
	public int getJobNum() {
		return idleTimeNum;
	}

	public void setFinishTime(double finishTime) {
		this.finishTime = finishTime;
	}

	public double getFinishTime() {
		return finishTime;
	}

	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}

	public double getStartTime() {
		return startTime;
	}

	@Override
	public double getCurrentOperationDueDate() {
		// Since there's no due date with the idle time,
		// we just set the operation due date to be infinite as well.
		return DUE_DATE;
	}

	public void setOps(Operation[] ops) {
		this.ops = ops;
	}

	@Override
	public Operation[] getOps() {
		return ops;
	}

	@Override
	public Job job(int i) {
		throw new UnsupportedOperationException("Not implemented for idle times.");
	}

	@Override
	public int numJobsInBatch() {
		return 1;
	}

	@Override
	public boolean isBatch() {
		return false;
	}


	//
	//
	// ValueStore implementation
	//
	//

	private HashMap<Object, Object> valueStore;

	@Override
	public void valueStorePut(Object key, Object value) {
		if (valueStore == null) {
			valueStore = new HashMap<Object, Object>();
		}
		valueStore.put(key, value);
	}

	@Override
	public Object valueStoreGet(Object key) {
		if (valueStore == null) {
			return null;
		} else {
			return valueStore.get(key);
		}
	}

	@Override
	public int valueStoreGetNumKeys() {
		return (valueStore == null) ? 0 : valueStore.size();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<Object> valueStoreGetAllKeys() {
		if (valueStore == null) {
			return Collections.EMPTY_SET;
		} else {
			return valueStore.keySet();
		}
	}

	@Override
	public Object valueStoreRemove(Object key) {
		if (valueStore == null) {
			return null;
		} else {
			return valueStore.remove(key);
		}
	}

	//
	//
	// Event notification
	//
	//
//
//	private NotifierAdapter<IdleTime, IdleTimeEvent> adapter = null;
//
//	@Override
//	public void addNotifierListener(NotifierListener<IdleTime, IdleTimeEvent> listener) {
//		if (adapter == null) {
//			adapter = new NotifierAdapter<IdleTime, IdleTimeEvent>(this);
//		}
//		adapter.addNotifierListener(listener);
//	}
//
//	@Override
//	public NotifierListener<IdleTime, IdleTimeEvent> getNotifierListener(int index) {
//		return adapter.getNotifierListener(index);
//	}
//
//	@Override
//	public void removeNotifierListener(NotifierListener<IdleTime, IdleTimeEvent> listener) {
//		adapter.removeNotifierListener(listener);
//	}
//
//	@Override
//	public int numListener() {
//		return adapter == null ? 0 : adapter.numListener();
//	}
//
//	protected void fire(IdleTimeEvent event) {
//		if (adapter != null) {
//			adapter.fire(event);
//		}
//	}

}
