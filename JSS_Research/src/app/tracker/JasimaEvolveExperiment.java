package app.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ec.Individual;
import ec.gp.GPIndividual;
import jasima.shopSim.core.PrioRuleTarget;

public class JasimaEvolveExperiment {

	private GPIndividual[] inds;
	
	private List<List<JasimaPriorityStat>> experimentDecisions;
	private Map<GPIndividual, List<JasimaPriorityStat>> experimentDecisionsMap;
	
	private Map<GPIndividual, JasimaPriorityStat> currentDecisionsMap;
	
	public JasimaEvolveExperiment(GPIndividual[] inds) {
		this.inds = inds;
		
		experimentDecisions = new ArrayList<List<JasimaPriorityStat>>(inds.length);
		experimentDecisionsMap = new HashMap<GPIndividual, List<JasimaPriorityStat>>(inds.length);
		
		for (GPIndividual ind : inds) {
			List<JasimaPriorityStat> priorityStat = new ArrayList<JasimaPriorityStat>();
			
			experimentDecisions.add(priorityStat);
			experimentDecisionsMap.put(ind, priorityStat);
		}
	}

	public void addDispatchingDecision() {
		for (GPIndividual ind : inds) {
			JasimaPriorityStat stat = new JasimaPriorityStat();
			
			currentDecisionsMap.put(ind, stat);
			experimentDecisionsMap.get(ind).add(stat);
		}
	}
	
	public void addPriority(int index, Individual ind, PrioRuleTarget entry, double priority) {
		currentDecisionsMap.get(ind).add(entry, priority);
	}
	
}
