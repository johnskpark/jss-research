package app.evolution;

import ec.EvolutionState;
import ec.gp.GPIndividual;

public class JasimaGPConfiguration {

	private EvolutionState state;
	private GPIndividual[] ind;
	private int[] subpopulations;
	private int threadnum;

	private JasimaGPData data;

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

	public void setData(JasimaGPData data) {
		this.data = data;
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

	public JasimaGPData getData() {
		return data;
	}

}
