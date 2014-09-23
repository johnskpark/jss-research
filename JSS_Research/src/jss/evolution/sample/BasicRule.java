package jss.evolution.sample;

import jss.Action;
import jss.ActionHandler;
import jss.IMachine;
import jss.IProblemInstance;
import ec.EvolutionState;
import ec.gp.GPIndividual;

public class BasicRule implements ActionHandler {

	// TODO Okay, find out what you don't need later down the line, and remove them.
	private EvolutionState state;
	private GPIndividual ind;
	private int subpopulation;
	private int threadnum;

	public BasicRule(EvolutionState state,
			GPIndividual ind,
			int subpopulation,
			int threadnum) {
		this.state = state;
		this.ind = ind;
		this.subpopulation = subpopulation;
		this.threadnum = threadnum;
	}

	@Override
	public Action getAction(IMachine machine, IProblemInstance problem) {

		return null;
	}

}
