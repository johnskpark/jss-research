package jss.problem;

import java.util.ArrayList;
import java.util.List;

import jss.Action;
import jss.IProblemInstance;
import jss.IResult;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class Result implements IResult {

	private List<Action> solutionActions = new ArrayList<Action>();

	private double makespan = 0;
	private double twt = 0;

	private IProblemInstance problem;

	/**
	 * TODO javadoc.
	 * @param problem
	 */
	public Result(IProblemInstance problem) {
		this.problem = problem;
	}

	@Override
	public void addAction(Action action) {
		solutionActions.add(action);
	}

	@Override
	public List<Action> getActions() {
		return solutionActions;
	}

	@Override
	public double getMakespan() {
		return makespan;
	}

	/**
	 * TODO javadoc.
	 * @param makespan
	 */
	public void setMakespan(double makespan) {
		if (this.makespan < makespan) {
			this.makespan = makespan;
		}
	}

	@Override
	public double getTWT() {
		return twt;
	}

	/**
	 * TODO javadoc.
	 * @param twt
	 */
	public void setTWT(double twt) {
		this.twt = twt;
	}

	public IProblemInstance getProblem() {
		return problem;
	}

}
