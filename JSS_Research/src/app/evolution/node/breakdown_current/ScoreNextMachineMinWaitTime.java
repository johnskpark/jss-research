package app.evolution.node.breakdown_current;

import app.evolution.JasimaGPData;
import app.node.NodeDefinition;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import jasima.shopSim.core.Operation;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;

public class ScoreNextMachineMinWaitTime extends AbsMBNode {

	private static final long serialVersionUID = 5028350516163746277L;
	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_NEXT_MACHINE_MIN_WAIT_TIME;

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
			double t = job.getShop().simTime();

			Operation nextOp = job.getOps()[nextTask];
			WorkStation nextMachine = nextOp.machine;

			double minWaitTime;

			// Check if the next machine is currently broken down.
			if (t <= getActivateTime(nextMachine) && t >= getDeactivateTime(nextMachine)) {
				System.out.println("Machine has broken down. " + nextMachine.machDat()[0].state);

				minWaitTime = getActivateTime(nextMachine) - t;
			} else {
				minWaitTime = 0.0;
			}

			// Add the leftover operation time to the minimum wait time.
			// TODO Still don't get how this part here works.
			if (nextMachine.machDat()[0].curJob != null) {
				System.out.println("Test print out.");

				// minWaitTime = minWaitTime + nextMachine.machDat()[0].
			}

			data.setPriority(minWaitTime);
		}
	}

}
