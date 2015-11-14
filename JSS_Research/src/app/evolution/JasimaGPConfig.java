package app.evolution;

import app.tracker.JasimaEvolveDecisionTracker;
import ec.EvolutionState;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;

public class JasimaGPConfig {

	private EvolutionState state;
	private GPIndividual[] ind;
	private int[] indIndices;
	private int[] subpopulations;
	private int threadnum;

	private GPProblem problem;
	private JasimaGPData data;

	private IJasimaTracker tracker;
	private JasimaEvolveDecisionTracker newTracker;

	// Getters

	public EvolutionState getState() {
		return state;
	}

	public GPIndividual[] getIndividuals() {
		return ind;
	}

	public int[] getIndIndices() {
		return indIndices;
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

	public JasimaEvolveDecisionTracker getNewTracker() {
		return newTracker;
	}

	// Setters

	public void setState(EvolutionState state) {
		this.state = state;
	}

	public void setIndividuals(GPIndividual[] ind) {
		this.ind = ind;
	}

	public void setIndIndices(int[] indIndices) {
		this.indIndices = indIndices;
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

	public void setNewTracker(JasimaEvolveDecisionTracker newTracker) {
		this.newTracker = newTracker;
	}

}
