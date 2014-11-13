package jss.problem;

import java.util.HashMap;
import java.util.Map;

import jss.IProblemInstance;
import jss.IResult;
import jss.problem.static_problem.StaticInstance;

/**
 * Helper class that deals with generating statistics for solutions.
 *
 * @author parkjohn
 *
 */
public class Statistics {

	private Map<IProblemInstance, IResult> problems = new HashMap<IProblemInstance, IResult>();

	private Map<String, Object> additionalData = new HashMap<String, Object>();

	private double totalMakespan = 0;
	private double totalTWT = 0;

	private double totalDeviation = 0;

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

		if (problem instanceof StaticInstance) {
			StaticInstance staticProblem = (StaticInstance) problem;
			totalDeviation += (solution.getMakespan() - staticProblem.getLowerBound()) /
					staticProblem.getLowerBound();
		}
	}

	/**
	 * TODO javadoc.
	 * @param key
	 * @param data
	 */
	public void addData(String key, Object data) {
		// TODO this needs to be modified
		additionalData.put(key, data);
	}

	public Object getData(String key) {
		// TODO this needs to be modified
		return additionalData.get(key);
	}

	/**
	 * Get the average makespan of the solutions.
	 */
	public double getAverageMakespan() {
		return totalMakespan / count;
	}

	/**
	 * Get the average total weighted tardiness.
	 */
	public double getAverageTWT() {
		return totalTWT / count;
	}

	/**
	 * Get the average deviation of the makespan of the solutions.
	 */
	public double getAverageMakespanDeviation() {
		return totalDeviation / count;
	}

}
