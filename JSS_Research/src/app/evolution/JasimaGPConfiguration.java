package app.evolution;

import ec.EvolutionState;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;

public class JasimaGPConfiguration {

	private EvolutionState state;
	private GPIndividual[] ind;
	private int[] subpopulations;
	private int threadnum;

	private GPProblem problem;
	private JasimaGPData data;

	private IJasimaTracker tracker;

	public void setState(EvolutionState state) {
		this.state = state;
	}

	public void setIndividuals(GPIndividual[] ind) {
		this.ind = ind;
	}

	public void setSubpopulations(int[] subpopulations) {
		this.subpopulations = subpopulations;
	}

	public void setThreadnum(int threadnum) {
		this.threadnum = threadnum;
	}

	public void setProblem(GPProblem problem) {
		this.problem = problem;
	}

	public void setData(JasimaGPData data) {
		this.data = data;
	}

	public void setTracker(IJasimaTracker tracker) {
		this.tracker = tracker;
	}

	public EvolutionState getState() {
		return state;
	}

	public GPIndividual[] getIndividuals() {
		return ind;
	}

	public int[] getSubpopulations() {
		return subpopulations;
	}

	public int getThreadnum() {
		return threadnum;
	}

	public GPProblem getProblem() {
		return problem;
	}

	public JasimaGPData getData() {
		return data;
	}

	public IJasimaTracker getTracker() {
		return tracker;
	}

}
