package jss.evolution.sample;

import jss.problem.IMachine;
import jss.problem.IProblemInstance;
import jss.solver.IAction;
import jss.solver.IRule;
import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;

public class BasicRule implements IRule {

	// TODO Okay, find out what you don't need later down the line, and remove them.
	private EvolutionState state;
	private Individual ind;
	private int subpopulation;
	private int threadnum;

	public BasicRule(EvolutionState state,
			Individual ind,
			int subpopulation,
			int threadnum) {
		this.state = state;
		this.ind = ind;
		this.subpopulation = subpopulation;
		this.threadnum = threadnum;
	}

	@Override
	public IAction getAction(IMachine machine, IProblemInstance problem) {
		// TODO


		return null;
	}

}
