package app.evolution.niched;

import app.evolution.JasimaGPIndividual;
import ec.EvolutionState;
import ec.Fitness;
import ec.util.Parameter;

public class JasimaNichedIndividual extends JasimaGPIndividual {

	private static final long serialVersionUID = 5720067179035222358L;

	public static final String P_NICHED_FITNESS = "niched-fitness";

	private Fitness nichedFitness;
	private int[] jobRanks;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
		nichedFitness = (Fitness) state.parameters.getInstanceForParameter(base.push(P_NICHED_FITNESS), null, Fitness.class);
		nichedFitness.setup(state, base.push(P_NICHED_FITNESS));
	}

	public Fitness getNichedFitness() {
		return nichedFitness;
	}

	public int[] getRuleDecisionVector() {
		return jobRanks;
	}

	public void setRuleDecisionVector(int[] jobRanks) {
		this.jobRanks = jobRanks;
	}

}
