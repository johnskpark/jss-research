package jss.evolution.sample;

import java.util.HashMap;
import java.util.Map;

import jss.IProblemInstance;
import jss.IResult;

public class BasicStatistics {

	private Map<IProblemInstance, IResult> problems = new HashMap<IProblemInstance, IResult>();

	private double totalMakespan = 0;
	private double totalTWT = 0;

	private int count = 0;

	public void addSolution(IProblemInstance problem, IResult solution) {
		problems.put(problem, solution);

		totalMakespan += solution.getMakespan();
		totalTWT += solution.getTWT();
		count++;
	}

	public double getAverageMakespan() {
		return totalMakespan / count;
	}

	public double getAverageTWT() {
		return totalTWT / count;
	}

}
