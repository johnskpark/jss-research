package app.evaluation.priorityRules;

import java.util.List;

import app.evaluation.AbsEvalPriorityRule;
import app.evaluation.JasimaEvalConfig;
import app.node.INode;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

public class MachineSpecificPriorityRule extends AbsEvalPriorityRule {

	private static final long serialVersionUID = -5435868205509788688L;

	private List<INode> rules;

	public MachineSpecificPriorityRule() {
		super();
	}

	@Override
	public void setConfiguration(JasimaEvalConfig config) {
		setSeed(config.getSeed());
		setNodeData(config.getNodeData());

		rules = config.getRules();
	}

	@Override
	public List<INode> getRuleComponents() {
		return rules;
	}

	@Override
	public int getNumRules() {
		return rules.size();
	}

	@Override
	public int getRuleSize(int index) {
		return rules.get(index).getSize();
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		int machineIndex = entry.getCurrMachine().index();

		getNodeData().setEntry(entry);

		return rules.get(machineIndex).evaluate(getNodeData());
	}

	@Override
	public void clear() {
		// Does nothing.
	}

	@Override
	public void jobSelected(PrioRuleTarget entry, PriorityQueue<?> q) {
		// Does nothing.
	}
	
}
