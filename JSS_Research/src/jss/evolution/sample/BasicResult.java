package jss.evolution.sample;

import java.util.ArrayList;
import java.util.List;

import jss.Action;
import jss.IProblemInstance;
import jss.IResult;

public class BasicResult implements IResult {

	private List<Action> solutionActions = new ArrayList<Action>();

	private double makespan = 0;
	private double twt = 0;

	private IProblemInstance problem;

	public BasicResult(IProblemInstance problem) {
		this.problem = problem;
	}

	@Override
	public void addAction(Action action) {
		solutionActions.add(action);
	}

	@Override
	public double getMakespan() {
		return makespan;
	}

	public void setMakespan(double makespan) {
		this.makespan = makespan;
	}

	@Override
	public double getTWT() {
		return twt;
	}

	public void setTWT(double twt) {
		this.twt = twt;
	}

	public IProblemInstance getProblem() {
		return problem;
	}

}
