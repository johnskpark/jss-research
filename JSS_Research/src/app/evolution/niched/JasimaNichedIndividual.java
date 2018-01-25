package app.evolution.niched;

import app.evolution.JasimaGPIndividual;
import ec.EvolutionState;
import ec.Fitness;
import ec.util.Parameter;

public class JasimaNichedIndividual extends JasimaGPIndividual {

	private static final long serialVersionUID = 5720067179035222358L;

	public static final String P_NICHED_FITNESS = "niched-fitness";

	private Fitness[] nichedFitness;
	private int[] jobRanks;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		JasimaNichedProblem problem = (JasimaNichedProblem) state.evaluator.p_problem;
		nichedFitness = new Fitness[problem.getNumNiches()];

		for (int i = 0; i < problem.getNumNiches(); i++) {
			nichedFitness[i] = (Fitness) state.parameters.getInstanceForParameter(base.push(P_NICHED_FITNESS), null, Fitness.class);
			nichedFitness[i].setup(state, base.push(P_NICHED_FITNESS));
		}
	}

	public Fitness getNichedFitness(int index) {
		return nichedFitness[index];
	}

	public int[] getRuleDecisionVector() {
		return jobRanks;
	}

	public void setRuleDecisionVector(int[] jobRanks) {
		this.jobRanks = jobRanks;
	}

	@Override
    public void printIndividualForHumans(final EvolutionState state, final int log) {
        state.output.println(EVALUATED_PREAMBLE + (evaluated ? "true" : "false"), log);

        state.output.print("Standard ", log);
        fitness.printFitnessForHumans(state,log);

        state.output.print("Niched ", log);
//        nichedFitness.printFitnessForHumans(state,log); // FIXME

        printTrees(state,log);
	}

}
