package jss.problem;

import java.util.HashMap;
import java.util.Map;

import jss.IProblemInstance;
import jss.IResult;

/**
 * Helper class that deals with generating statistics for solutions.
 *
 * @author parkjohn
 *
 */
public class Statistics {

	private Map<IProblemInstance, IResult> problems = new HashMap<IProblemInstance, IResult>();

	private double totalMakespan = 0;
	private double totalTWT = 0;

	private int count = 0;

	/**
	 * Generate a new instance of Statistics object.
	 */
	public Statistics() {
	}

	/**
	 * Add a solution for a Job Shop Scheduling problem instance into the
	 * solution repository.
	 * @param problem The solved problem instance.
	 * @param solution The solution for the solved problem instance.
	 */
	public void addSolution(IProblemInstance problem, IResult solution) {
		problems.put(problem, solution);

		totalMakespan += solution.getMakespan();
		totalTWT += solution.getTWT();
		count++;
	}

	/**
	 * Get the average makespan of the solutions.
	 * @return
	 */
	public double getAverageMakespan() {
		return totalMakespan / count;
	}

	/**
	 * Get the average total weighted tardiness.
	 * @return
	 */
	public double getAverageTWT() {
		return totalTWT / count;
	}

}
