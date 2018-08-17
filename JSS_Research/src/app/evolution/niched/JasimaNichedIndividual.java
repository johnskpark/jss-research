package app.evolution.niched;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.evolution.JasimaGPIndividual;
import ec.EvolutionState;
import ec.util.Parameter;

public class JasimaNichedIndividual extends JasimaGPIndividual {

	private static final long serialVersionUID = 5720067179035222358L;

	public static final String P_NICHED_FITNESS = "niched-fitness";

	public static final double NOT_SET = -1;

	private List<Double> nichedFitness = new ArrayList<Double>();
	private int numNiches;

	private int[] jobRanks;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		JasimaNichedProblem problem = (JasimaNichedProblem) state.evaluator.p_problem;
		numNiches = problem.getNumNiches();
	}

	public void initNichedFitness() {
		nichedFitness = new ArrayList<>(numNiches);

		for (int i = 0; i < numNiches; i++) {
			nichedFitness.add(NOT_SET);
		}
	}

	public double getNichedFitness(int index) {
		return nichedFitness.get(index);
	}

	public void setNichedFitness(int index, double fitness) {
		nichedFitness.set(index, fitness);
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

        state.output.println("Niched fitness: " + nichedFitness, log);

        state.output.print("Niched ", log);
        printTrees(state,log);

//      nichedFitness.printFitnessForHumans(state,log); // FIXME
	}

	@Override
	public Object clone() {
		JasimaNichedIndividual newObject = (JasimaNichedIndividual) super.clone();

		newObject.nichedFitness = new ArrayList<>(this.nichedFitness);
		newObject.numNiches = this.numNiches;

		newObject.jobRanks = Arrays.copyOf(this.jobRanks, this.jobRanks.length);

		return newObject;
	}

}
