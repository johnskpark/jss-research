package app.evolution.coop;

import app.evolution.JasimaReproducible;
import ec.Individual;

public interface JasimaCoopIndividual extends JasimaReproducible {

	public Individual[] getCollaborators();

	public void setCollaborators(Individual[] inds);

}
