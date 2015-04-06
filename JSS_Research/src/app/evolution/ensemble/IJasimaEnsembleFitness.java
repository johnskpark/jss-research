package app.evolution.ensemble;

import app.evolution.IJasimaFitness;
import app.evolution.IJasimaTracker;

public interface IJasimaEnsembleFitness extends IJasimaFitness {

	public void setTracker(IJasimaTracker tracker);

}
