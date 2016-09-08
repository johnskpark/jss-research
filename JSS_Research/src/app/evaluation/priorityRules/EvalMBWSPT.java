package app.evaluation.priorityRules;

import java.util.List;

import app.evaluation.EvalPriorityRuleBase;
import app.evaluation.JasimaEvalConfig;
import app.node.INode;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

public class EvalMBWSPT extends EvalPriorityRuleBase {

	@Override
	public void setConfiguration(JasimaEvalConfig config) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<INode> getRuleComponents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		super.beforeCalc(q);

		// TODO Auto-generated method stub
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumRules() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getRuleSize(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<PrioRuleTarget> getEntryRankings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void jobSelected(PrioRuleTarget entry, PriorityQueue<?> q) {
		// Does nothing.
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

}
