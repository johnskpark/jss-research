package app.evolution.ensemble;

import jasima.shopSim.core.PrioRuleTarget;
import ec.gp.GPIndividual;

public class EnsembleTrackerValue {

	// TODO Let's go with this for now. Premature optimisation is the bane of existence.
	private GPIndividual[] individuals;
	private PrioRuleTarget[] entries;

	private double[][] priorities;
	private int[] ensembleRanking;

	public EnsembleTrackerValue() {
	}

	public GPIndividual[] getIndividuals() {
		return individuals;
	}

	public void setIndividuals(GPIndividual[] individuals) {
		this.individuals = individuals;
	}

	public PrioRuleTarget[] getEntries() {
		return entries;
	}

	public void setEntries(PrioRuleTarget[] entries) {
		this.entries = entries;
	}

	public double[][] getPriorities() {
		return priorities;
	}

	public void setPriorities(double[][] priorities) {
		this.priorities = priorities;
	}

	public int[] getEnsembleRanking() {
		return ensembleRanking;
	}

	public void setEnsembleRanking(int[] ensembleRanking) {
		this.ensembleRanking = ensembleRanking;
	}

	public void clear() {
		individuals = null;
		entries = null;
		priorities = null;
		ensembleRanking = null;
	}

}
