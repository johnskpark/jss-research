package app.evolution.simple;

import java.util.Map;

import app.evolution.IJasimaFitness;

public interface IJasimaSimpleFitness extends IJasimaFitness {

	public void accumulateFitness(final Map<String, Object> results);

}
