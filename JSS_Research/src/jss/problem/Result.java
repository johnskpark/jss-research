package jss.problem;

import java.util.ArrayList;
import java.util.List;

import jss.Action;
import jss.IProblemInstance;
import jss.IResult;

/**
 * A basic concrete representation of the @see IResult interface.
 *
 * This Result is built iteratively as the simulation of the Job Shop
 * Scheduling environment is being carried out.
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
	 * Generate a new instance of the Result object.
	 * @param problem The problem instance for this Result is being generated
	 *                for.
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
	 * Update the current makespan for the partial solution.
	 * @param makespan The makespan to update to.
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
	 * Update the current total weighted tardiness for the partial solution.
	 * @param twt The total weighted tardiness to update to.
	 */
	public void setTWT(double twt) {
		this.twt = twt;
	}

	public IProblemInstance getProblem() {
		return problem;
	}

}
