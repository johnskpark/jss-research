package app.evaluation.priorityRules.idle;

import java.util.List;

import app.evaluation.JasimaEvalConfig;
import app.node.INode;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

public class EvalSPTWithIdle extends PriorityRuleWithIdleBase {

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setConfiguration(JasimaEvalConfig config) { 
		super.setConfiguration(config);
		
		// TODO 
	}

	@Override
	public List<INode> getRuleComponents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double calcIdlePrio(PriorityQueue<?> q) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double calcIdleTime(PriorityQueue<?> q) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double calcJobPrio(PrioRuleTarget entry) {
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
		// TODO Auto-generated method stub
		
	}

}
