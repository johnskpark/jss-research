package app.evaluation;

import java.util.Map;

public interface IJasimaEvalFitness {

	public String getHeaderName();
	
	public double getRelevantResult(final Map<String, Object> results);

}
