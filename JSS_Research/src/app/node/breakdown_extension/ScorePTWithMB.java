package app.node.breakdown_extension;

import app.node.NodeAnnotation;
import app.node.NodeData;
import app.node.NodeDefinition;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;

@NodeAnnotation(node=NodeDefinition.SCORE_PT_WITH_MB)
public class ScorePTWithMB extends AbsMBNode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_PT_WITH_MB;

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

		WorkStation machine = job.getCurrMachine();
		double pt = job.getCurrentOperation().procTime;
		double t = job.getShop().simTime();

		if (t + pt <= getDeactivateTime(machine)) {
			return pt;
		} else {
			return pt + getNextRepairTime(machine);
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
