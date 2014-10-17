package jss.evolution;

import ec.EvolutionState;
import ec.gp.GPIndividual;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class JSSGPConfiguration {

	private EvolutionState state;
	private GPIndividual[] ind;
	private int[] subpopulations;
	private int threadnum;

	private JSSGPData data;

	/**
	 * TODO javadoc.
	 */
	public JSSGPConfiguration() {
	}

	/**
	 * TODO javadoc.
	 * @param state
	 */
	public void setState(EvolutionState state) {
		this.state = state;
	}

	/**
	 * TODO javadoc.
	 * @param ind
	 */
	public void setIndividuals(GPIndividual[] ind) {
		this.ind = ind;
	}

	/**
	 * TODO javadoc.
	 * @param subpopulation
	 */
	public void setSubpopulations(int[] subpopulations) {
		this.subpopulations = subpopulations;
	}

	/**
	 * TODO javadoc.
	 * @param threadnum
	 */
	public void setThreadnum(int threadnum) {
		this.threadnum = threadnum;
	}

	/**
	 * TODO javadoc.
	 * @param data
	 */
	public void setData(JSSGPData data) {
		this.data = data;
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	public EvolutionState getState() {
		return state;
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	public GPIndividual[] getIndividuals() {
		return ind;
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	public int[] getSubpopulations() {
		return subpopulations;
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	public int getThreadnum() {
		return threadnum;
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	public JSSGPData getData() {
		return data;
	}
}
