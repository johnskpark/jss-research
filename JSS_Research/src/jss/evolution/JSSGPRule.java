package jss.evolution;

import jss.IActionHandler;
import ec.EvolutionState;
import ec.gp.GPIndividual;

/**
 * TODO javadoc. Also, this should go in sample.
 *
 * @author parkjohn
 *
 */
public abstract class JSSGPRule implements IActionHandler {

	private EvolutionState state;
	private GPIndividual[] inds;
	private int threadnum;

	private JSSGPData data;

	/**
	 * TODO javadoc.
	 * @param state
	 * @param ind
	 * @param threadnum
	 * @param data
	 */
	public JSSGPRule(EvolutionState state,
			GPIndividual[] inds,
			int threadnum,
			JSSGPData data) {
		this.state = state;
		this.inds = inds;
		this.threadnum = threadnum;

		this.data = data;
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	protected EvolutionState getState() {
		return state;
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	protected GPIndividual[] getIndividuals() {
		return inds;
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	protected int getThreadnum() {
		return threadnum;
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	protected JSSGPData getData() {
		return data;
	}

}
