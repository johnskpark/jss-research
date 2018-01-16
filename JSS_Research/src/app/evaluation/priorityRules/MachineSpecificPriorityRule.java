package app.evaluation.priorityRules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.evaluation.EvalPriorityRuleBase;
import app.evaluation.JasimaEvalConfig;
import app.node.INode;
import jasima.shopSim.core.PrioRuleTarget;

public class MachineSpecificPriorityRule extends EvalPriorityRuleBase {

	private static final long serialVersionUID = -5435868205509788688L;

	private List<INode> rules;

	private List<PrioRuleTarget> entries = new ArrayList<PrioRuleTarget>();
	private Map<PrioRuleTarget, Double> entryPrios = new HashMap<>();

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

		double prio = rules.get(machineIndex).evaluate(getNodeData());

		entries.add(entry);
		entryPrios.put(entry, prio);

		return prio;
	}

	@Override
	public void clear() {
		entries.clear();
		entryPrios.clear();
	}

}
