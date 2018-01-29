package app.evolution.niched;

import java.util.Arrays;

import app.evolution.JasimaGPIndividual;
import ec.EvolutionState;
import ec.util.Parameter;

public class JasimaNichedIndividual extends JasimaGPIndividual {

	private static final long serialVersionUID = 5720067179035222358L;

	public static final String P_NICHED_FITNESS = "niched-fitness";

	private double[] nichedFitnesses;
	private int numNiches;
	
	private int[] jobRanks;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		JasimaNichedProblem problem = (JasimaNichedProblem) state.evaluator.p_problem;
		numNiches = problem.getNumNiches();
	}

	public void initNichedFitness() {
		nichedFitnesses = new double[numNiches];
	}
	
	public double getNichedFitness(int index) {
		return nichedFitnesses[index];
	}
	
	public void setNichedFitness(int index, double fitness) {
		nichedFitnesses[index] = fitness;
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
	
	@Override
	public Object clone() {
		JasimaNichedIndividual newObject = (JasimaNichedIndividual) super.clone();
		
		newObject.nichedFitnesses = Arrays.copyOf(this.nichedFitnesses, this.nichedFitnesses.length);
		newObject.numNiches = this.numNiches;
		
		newObject.jobRanks = Arrays.copyOf(this.jobRanks, this.jobRanks.length);
		
		return newObject;
	}

}
