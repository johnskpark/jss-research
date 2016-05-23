package app.evaluation.fitness;

import java.util.Map;

import app.evaluation.IJasimaEvalFitness;
import app.tracker.JasimaExperimentTracker;
import jasima.shopSim.core.PR;

public class DiversityFitness implements IJasimaEvalFitness {

	@Override
	public String getHeaderName() {
		// TODO Auto-generated method stub
		// This needs to include everything that I thought of measuring.
		return null;
	}

	@Override
	public boolean resultIsNumeric() {
		return false;
	}

	@Override
	public double getNumericResult(PR rule, Map<String, Object> results, JasimaExperimentTracker tracker) {
		throw new UnsupportedOperationException("The output is not numeric!");
	}

	@Override
	public String getStringResult(PR rule, Map<String, Object> results, JasimaExperimentTracker tracker) {
		// TODO Auto-generated method stub
		return null;
	}

}
