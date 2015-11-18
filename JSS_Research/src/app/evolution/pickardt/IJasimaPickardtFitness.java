package app.evolution.pickardt;

import java.util.Map;

import app.evolution.IJasimaFitness;

public interface IJasimaPickardtFitness extends IJasimaFitness<JasimaVectorIndividual> {

	public void accumulateFitness(final int index, final Map<String, Object> results);

}
