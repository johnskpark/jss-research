package app.evolution;

import ec.gp.GPIndividual;

public class GroupedIndividual {

	private GPIndividual[] inds;

	private boolean evaluated = false;

	public GroupedIndividual(GPIndividual[] inds) {
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
