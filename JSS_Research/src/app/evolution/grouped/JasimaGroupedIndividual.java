package app.evolution.grouped;

import ec.gp.GPIndividual;

public class JasimaGroupedIndividual {

	private GPIndividual[] inds;

	private boolean evaluated = false;

	public JasimaGroupedIndividual(GPIndividual[] inds) {
		this.inds = inds;
	}

	public GPIndividual[] getInds() {
		return inds;
	}

	public boolean isEvaluated() {
		return evaluated;
	}

	public void setEvaluated(boolean evaluated) {
		this.evaluated = evaluated;
	}

}
