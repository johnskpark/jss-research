package app.node.breakdown_extension;

import app.node.NodeAnnotation;
import app.node.NodeData;
import app.node.NodeDefinition;
import jasima.shopSim.core.Operation;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;

@NodeAnnotation(node=NodeDefinition.SCORE_NPT_WITH_MB)
public class ScoreNPTWithMB extends AbsMBNode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_NPT_WITH_MB;

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public int getSize() {
		return NODE_DEFINITION.numChildren() + 1;
	}

	@Override
	public double evaluate(NodeData data) {
		PrioRuleTarget job = data.getPrioRuleTarget();

		int nextTask = job.getTaskNumber() + 1;
		if (nextTask >= job.numOps()) {
			return 0.0;
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
				return npt;
			} else {
				return npt + getNextRepairTime(nextMachine);
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		return o != null && o.getClass() == this.getClass();
	}

	@Override
	public String toString() {
		return NODE_DEFINITION.toString();
	}

}
