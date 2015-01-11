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
	 * Add in the additional data into the statistics measure.
	 * @param key The key to extract the data with.
	 * @param data The object representing the data.
	 */
	public <T> void addData(String key, T data) {
		additionalData.put(key, data);
	}

	/**
	 * Retrieve the additional data from the statistics measure.
	 * @param key The key to extract the data with.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getData(String key) {
		return (T)additionalData.get(key);
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
