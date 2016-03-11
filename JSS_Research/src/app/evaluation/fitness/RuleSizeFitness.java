package app.evaluation.fitness;

import java.util.Map;

import app.evaluation.AbsEvalPriorityRule;
import app.evaluation.IJasimaEvalFitness;

public class RuleSizeFitness implements IJasimaEvalFitness {

	@Override
	public String getHeaderName() {
		return "NumRules,NumNodes";
	}

	@Override
	public String getRelevantResult(final AbsEvalPriorityRule solver, final Map<String, Object> results) {
		int numRules = solver.getNumRules();
		String output = numRules + ",\"";

		for (int i = 0; i < numRules; i++) {
			if (i != 0) {
				output += ",";
			}

			output += solver.getRuleSize(i);
		}

		output += "\"";


		return output;
	}

}
