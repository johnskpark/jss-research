package app.evolution.node.breakdown_extension;

import app.evolution.JasimaGPData;
import app.node.NodeDefinition;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import jasima.shopSim.core.IndividualMachine;
import jasima.shopSim.core.Job;
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
			WorkStation nextMachine = job.getOps()[nextTask].machine;

			data.setPriority(getWorkInQueue(job, nextMachine));
		}
	}

	private double getWorkInQueue(PrioRuleTarget job, WorkStation nextMachine) {
		PriorityQueue<Job> queue = nextMachine.queue;

		// WINQ = all the jobs in the queue and the time left on the current job.
		double winq = 0.0;
		for (int i = 0; i < queue.size(); i++) {
			winq += getProcTime(queue.get(i), nextMachine);
		}

		IndividualMachine indMachine = nextMachine.machDat()[0];

		if (indMachine.procFinished > job.getShop().simTime()) {
			// Calculate the work remaining on the current job being processed.
			double workRemaining;
			if (indMachine.procFinished <= getDeactivateTime(nextMachine)) {
				workRemaining = indMachine.procFinished - job.getShop().simTime();
			} else {
				workRemaining = indMachine.procFinished - job.getShop().simTime() + getNextRepairTime(nextMachine);
			}
			winq += workRemaining;
		}

		return winq;
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
