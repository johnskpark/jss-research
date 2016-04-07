package test;

import java.util.ArrayList;
import java.util.List;

import jasima.shopSim.core.Job;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.core.WorkStation;
import jasima.shopSim.prioRules.basic.SPT;

public class TrackedSPT extends SPT {

	private static final long serialVersionUID = -1824459398825502173L;

	private static List<Job> jobs = new ArrayList<Job>();
	private static WorkStation machine = null;

	private static List<Integer> taskNums = new ArrayList<Integer>();
	private static List<Integer[]> machines = new ArrayList<Integer[]>();
	private static double simTime = 0;
	private static List<Double> priorities = new ArrayList<Double>();

	private static boolean notSet = false;

	public static List<Job> getJobs() { return jobs; }
	public static WorkStation getMachine() { return machine; }

	public static List<Integer> getTaskNums() { return taskNums; }
	public static List<Integer[]> getMachines() { return machines; }
	public static double getSimTime() { return simTime; }
	public static List<Double> getPriorities() { return priorities; }

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		super.beforeCalc(q);

		try {
			if (q.size() >= 10 && !notSet) {
				// Clone the decision scenario and add them somewhere.
				for (int i = 0; i < q.size(); i++) {
					Job job = (Job) q.get(i);

					jobs.add((Job) job.clone());
					machine = job.getCurrMachine();

					int taskNum = job.getTaskNumber();
					taskNums.add(job.getTaskNumber());

					Integer[] m = new Integer[job.numOpsLeft()];
					for (int j = taskNum; j < job.numOps(); j++) {
						m[j-taskNum] = job.getOps()[j].machine.index();
					}
					machines.add(m);

					priorities.add(calcPrio(job));
				}

				simTime = q.get(0).getShop().simTime();

				notSet = true;
			}
		} catch (CloneNotSupportedException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		return super.calcPrio(entry);
	}

}
