package app.evolution.node.breakdown_extension;

import app.evolution.JasimaGPData;
import app.node.NodeDefinition;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import jasima.shopSim.core.Job;
import jasima.shopSim.core.Operation;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.core.WorkStation;

public class ScoreWINQWithMB extends AbsMBNode {

	private static final long serialVersionUID = 6003909555969314625L;
	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_WINQ_WITH_MB;

	@Override
	public String toString() {
		return NODE_DEFINITION.toString();
	}

	@Override
	public int expectedChildren() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public void eval(final EvolutionState state,
			final int thread,
			final GPData input,
			final ADFStack stack,
			final GPIndividual individual,
			final Problem problem) {
		JasimaGPData data = (JasimaGPData) input;
		PrioRuleTarget job = data.getPrioRuleTarget();

		int nextTask = job.getTaskNumber() + 1;
		if (nextTask >= job.numOps()) {
			data.setPriority(0.0);
		} else {
			Operation nextOp = job.getOps()[nextTask];
			WorkStation nextMachine = nextOp.machine;

			// TODO winq2, winq3 and err are just for test purposes
			// So its done by procFinished, which is going by what job is currently on the machine.

			PriorityQueue<Job> queue = nextMachine.queue;
			double winq = 0.0;
			double winq2 = 0.0;

			// TODO temporary.
			System.out.printf("Queue: %d, num jobs waiting: %d, sim time: %f\n", queue.size(), nextMachine.numJobsWaiting(), nextMachine.shop().simTime());

			for (int i = 0; i < queue.size(); i++) {
				PrioRuleTarget jobInNextQueue = queue.get(i);
				winq += getProcTime(jobInNextQueue, nextMachine);
				winq2 += jobInNextQueue.getCurrentOperation().procTime;
			}

			// TODO temporary.
			PrioRuleTarget jub = nextMachine.machDat()[0].curJob;
			if (jub != null) {
				System.out.printf("Machine id: %d, Jub op: %d, m_id: %d, proc: %f\n", nextMachine.index(), jub.getTaskNumber(), jub.getOps()[0].machine.index(), jub.getOps()[0].procTime);
			}

			double winq3 = nextMachine.workContent(false);
			double err = 0.001;
			if (winq2 - err > winq3 || winq2 + err < winq3) {
				throw new RuntimeException("WINQ values do not match: " + winq2 + ", " + winq3);
			}

			data.setPriority(winq);
		}
	}

	private double getProcTime(PrioRuleTarget job, WorkStation machine) {
		double pt = job.getCurrentOperation().procTime;
		if (job.getShop().simTime() + pt <= getDeactivateTime(machine)) {
			return pt;
		} else {
			return pt + getNextRepairTime(machine);
		}
	}

}
