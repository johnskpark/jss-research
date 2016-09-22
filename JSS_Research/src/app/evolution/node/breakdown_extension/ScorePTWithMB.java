package app.evolution.node.breakdown_extension;

import app.evolution.JasimaGPData;
import app.node.NodeDefinition;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;

public class ScorePTWithMB extends AbsMBNode {

	private static final long serialVersionUID = 809891465301720083L;
	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_PT_WITH_MB;

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

		WorkStation machine = job.getCurrMachine();
		double pt = job.getCurrentOperation().procTime;
		double t = job.getShop().simTime();

		if (t + pt <= getDeactivateTime(machine)) {
			data.setPriority(pt);
		} else {
			data.setPriority(pt + getNextRepairTime(machine));
		}
	}

}
