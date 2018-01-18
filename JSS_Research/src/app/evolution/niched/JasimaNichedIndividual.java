package app.evolution.niched;

import app.evolution.JasimaGPIndividual;
import ec.EvolutionState;
import ec.Fitness;
import ec.util.Parameter;

public class JasimaNichedIndividual extends JasimaGPIndividual {

	private static final long serialVersionUID = 5720067179035222358L;

	private Fitness nichedFitness;
	private int[] jobRanks;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
		// TODO setup the niched fitness here.
		throw new RuntimeException("This isn't ready yet.");
	}

	public Fitness getNichedFitness() {
		return nichedFitness; // TODO this needs to be initialised as well.
	}

	public int[] getRuleDecisionVector() {
		return jobRanks;
	}

	public void setRuleDecisionVector(int[] jobRanks) {
		this.jobRanks = jobRanks;
	}

}
