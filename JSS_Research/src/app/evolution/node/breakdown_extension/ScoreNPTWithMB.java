package app.evolution.node.breakdown_extension;

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

public class ScoreNPTWithMB extends AbsMBNode {

	private static final long serialVersionUID = 9120708992405276613L;
	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_NPT_WITH_MB;

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

			WorkStation currMachine = job.getCurrMachine();
			double pt = job.getCurrentOperation().procTime;
			double t = job.getShop().simTime();

			double actualPT = pt;
			if (t + pt > getDeactivateTime(currMachine)) {
				actualPT = pt + getNextRepairTime(currMachine);
			}

			WorkStation nextMachine = nextOp.machine;
			double npt = nextOp.procTime;
			if (t + actualPT + npt <= getDeactivateTime(nextMachine)) {
				data.setPriority(npt);
			} else {
				data.setPriority(npt + getNextRepairTime(nextMachine));
			}
		}
	}

}
