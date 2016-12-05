package app.evaluation.priorityRules.idle;

import app.evaluation.EvalPriorityRuleBase;
import app.evaluation.JasimaEvalConfig;
import jasima.shopSim.core.IdleTime;
import jasima.shopSim.core.Operation;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.core.WorkStation;

public abstract class PriorityRuleWithIdleBase extends EvalPriorityRuleBase {

	private static final long serialVersionUID = 2191207420087763065L;

	private boolean includeIdleTimes = false;

	private IdleTime currIdleTime = null;
	private double currIdlePrio = Double.NEGATIVE_INFINITY;

	@Override
	public void setConfiguration(JasimaEvalConfig config) {
		// Just include the idle times.
		includeIdleTimes = config.getIncludeIdleTimes();
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		super.beforeCalc(q);

		clear();

		if (includeIdleTimes && currIdleTime == null) {
			currIdlePrio = calcIdleTime(q);
			currIdleTime = generateIdleTime(q.getWorkStation(), currIdlePrio);

			// TODO need to put this into the queue somehow.
			// Well fuck I can't seem to add this to the queue
			// because the queue in the workstation is a job.
			
			// TODO I have a feeling that this will not work the first time,
			// so will need extensive testing.

			// This is going to result in an infinite loop,
			// need to think of this carefully.
			assert q.equals(q.getWorkStation().queue);
			q.getWorkStation().queue.add(currIdleTime);
		}
	}

	public IdleTime generateIdleTime(WorkStation currMachine, double idleTime) {
		IdleTime idleTimeEntry = new IdleTime(currMachine.shop());

		idleTimeEntry.setArriveTime(currMachine.shop().simTime());
		idleTimeEntry.setRelDate(currMachine.shop().simTime());

		// Let's just worry about procTime and machine for now.
		idleTimeEntry.setOps(new Operation[]{
				new Operation() {{
					machine = currMachine;
					procTime = idleTime;
				}}
		});

		return idleTimeEntry;
	}

	public abstract double calcIdlePrio(PriorityQueue<?> q);

	public abstract double calcIdleTime(PriorityQueue<?> q);
	
	public abstract double calcJobPrio(PrioRuleTarget entry);

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		if (entry instanceof IdleTime) {
			return currIdlePrio;
		} else {
			return calcJobPrio(entry);
		}
	}

}
