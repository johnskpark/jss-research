package app.evaluation;

import java.util.Map;

public interface IJasimaEvalFitness {

	public String getHeaderName();

	public String getRelevantResult(final AbsEvalPriorityRule solver, final Map<String, Object> results);

}
