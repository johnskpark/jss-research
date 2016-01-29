package app.evolution.multilevel_new;

import java.util.ArrayList;
import java.util.List;

import app.evolution.JasimaGPIndividual;
import ec.EvolutionState;
import ec.multilevel_new.MLSStatistics;
import ec.multilevel_new.MLSSubpopulation;
import ec.util.Parameter;
import jasima.core.statistics.SummaryStat;

public class JasimaMultilevelNichingStatistics extends MLSStatistics implements IJasimaMultilevelFitnessListener{

	private static final long serialVersionUID = 1831159170439048338L;

	private List<SummaryStat> individualFitnesses = new ArrayList<SummaryStat>();
	private List<SummaryStat> ensembleFitnesses = new ArrayList<SummaryStat>();
	private List<SummaryStat> diversities = new ArrayList<SummaryStat>();

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
	}

	@Override
	public void addFitness(Object entity, int index, double value) {
		// Add the instance statistics.
		if (entity instanceof JasimaGPIndividual) {
			individualFitnesses.get(index).value(value);
		} else if (entity instanceof MLSSubpopulation) {
			ensembleFitnesses.get(index).value(value);
		} else {
			diversities.get(index).value(value);
		}
	}

	@Override
	public void preEvaluationStatistics(final EvolutionState state) {
		super.preEvaluationStatistics(state);

		JasimaMultilevelProblem problem = (JasimaMultilevelProblem) state.evaluator.p_problem;

		for (int i = 0; i < problem.getSimConfig().getNumConfigs(); i++) {
			individualFitnesses.add(new SummaryStat());
			ensembleFitnesses.add(new SummaryStat());
			diversities.add(new SummaryStat());
		}
	}

	@Override
	public void postEvaluationStatistics(final EvolutionState state) {
		super.postEvaluationStatistics(state);

		// Carry out the statistics for the individual training instances.
		instanceStatistics(state);

		individualFitnesses.clear();
		ensembleFitnesses.clear();
		diversities.clear();
	}

	protected void instanceStatistics(final EvolutionState state) {
		// Print out the instance statistics.
		// TODO

		state.output.print("Individual Fitnesses per Instance (min,avg,max): ", statisticsLog);
		state.output.println("", statisticsLog);

		state.output.print("Ensemble Fitnesses per Instance (min,avg,max): ", statisticsLog);
		state.output.println("", statisticsLog);

		state.output.print("Diversity per Instance (min,avg,max): ", statisticsLog);
		state.output.println("", statisticsLog);
	}

}
