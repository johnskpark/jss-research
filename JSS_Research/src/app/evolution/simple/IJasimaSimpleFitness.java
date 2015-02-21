package app.evolution.simple;

import java.util.Map;

import app.evolution.IJasimaFitness;
import app.evolution.IJasimaGPProblem;

public interface IJasimaSimpleFitness extends IJasimaFitness {

	public void setProblem(IJasimaGPProblem problem);
	
	public void accumulateFitness(final int index, final Map<String, Object> results);

}
