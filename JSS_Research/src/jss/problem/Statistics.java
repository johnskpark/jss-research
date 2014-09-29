package jss.problem;

import java.util.HashMap;
import java.util.Map;

import jss.IProblemInstance;
import jss.IResult;

/**
 * TODO javadoc.
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
	 * TODO javadoc.
	 */
	public Statistics() {
	}

	/**
	 * TODO javadoc.
	 * @param problem
	 * @param solution
	 */
	public void addSolution(IProblemInstance problem, IResult solution) {
		problems.put(problem, solution);

		totalMakespan += solution.getMakespan();
		totalTWT += solution.getTWT();
		count++;
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	public double getAverageMakespan() {
		return totalMakespan / count;
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	public double getAverageTWT() {
		return totalTWT / count;
	}

}
