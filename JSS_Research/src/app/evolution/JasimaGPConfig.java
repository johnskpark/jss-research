package app.evolution;

import app.simConfig.SimConfig;
import app.tracker.JasimaEvolveExperimentTracker;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPProblem;

public class JasimaGPConfig {

	private EvolutionState state;
	private Individual[] ind;
	private int[] indIndices;
	private int[] subpopulations;
	private int threadnum;

	private GPProblem problem;
	private JasimaGPData data;

	private JasimaEvolveExperimentTracker tracker;

	private SimConfig simConfig;
	private int simIndex;

	// Getters

	public EvolutionState getState() {
		return state;
	}

	public Individual[] getIndividuals() {
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

	public boolean hasTracker() {
		return tracker != null;
	}

	public JasimaEvolveExperimentTracker getTracker() {
		return tracker;
	}

	public SimConfig getSimConfig() {
		return simConfig;
	}

	public int getSimIndex() {
		return simIndex;
	}

	// Setters

	public void setState(EvolutionState state) {
		this.state = state;
	}

	public void setIndividuals(Individual[] ind) {
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

	public void setTracker(JasimaEvolveExperimentTracker newTracker) {
		this.tracker = newTracker;
	}

	public void setSimConfig(SimConfig simConfig) {
		this.simConfig = simConfig;
	}

	public void setSimIndex(int simIndex) {
		this.simIndex = simIndex;
	}

}
