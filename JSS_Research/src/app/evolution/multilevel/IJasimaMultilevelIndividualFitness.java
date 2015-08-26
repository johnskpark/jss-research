package app.evolution.multilevel;

import java.util.Map;

import app.evolution.IJasimaFitness;

public interface IJasimaMultilevelIndividualFitness extends IJasimaFitness {

	public void accumulateFitness(int expIndex, Map<String, Object> results);

}
