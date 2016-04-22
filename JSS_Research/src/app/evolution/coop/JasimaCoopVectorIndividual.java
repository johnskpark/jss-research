package app.evolution.coop;

import app.evolution.JasimaVectorIndividual;
import ec.Individual;

public class JasimaCoopVectorIndividual extends JasimaVectorIndividual implements JasimaCoopIndividual {

	private static final long serialVersionUID = 2080091437257636271L;

	public Individual[] collaborators;

	@Override
	public Individual[] getCollaborators() {
		return collaborators;
	}

	@Override
	public void setCollaborators(Individual[] inds) {
		collaborators = inds;
	}

}
