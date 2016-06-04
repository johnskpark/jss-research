package app.evaluation;

import java.util.Map;

import app.node.INode;
import app.tracker.JasimaExperimentTracker;
import jasima.shopSim.core.PR;

public interface IJasimaEvalFitness {

	public String getHeaderName();

	public boolean resultIsNumeric();

	public double getNumericResult(final PR rule, final int configIndex, final Map<String, Object> results, JasimaExperimentTracker<INode> tracker);

	public String getStringResult(final PR rule, final int configIndex, final Map<String, Object> results, JasimaExperimentTracker<INode> tracker);

}
