package app.evolution.simple;

import java.util.Map;

import app.evolution.IJasimaFitness;
import app.evolution.JasimaGPIndividual;
import app.evolution.JasimaGPProblem;

public interface IJasimaSimpleFitness extends IJasimaFitness<JasimaGPIndividual> {

	public void setProblem(JasimaGPProblem problem);

	public void accumulateFitness(final int index, final Map<String, Object> results);

}
