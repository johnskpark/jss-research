package app.evolution.multitask;

import java.util.List;

import app.simConfig.DynamicBreakdownSimConfig;
import ec.simple.SimpleEvolutionState;

public class MultitaskEvolutionState extends SimpleEvolutionState {

	private static final long serialVersionUID = 8005628891176535079L;

	public static final int NOT_SET = -1;

	private DynamicBreakdownSimConfig simConfig;
	private int numTasks;

	private int[][] tasksForInds;

	private List<Integer>[][] indsPerTask;

	public void setNumTasks(int numTasks) {
		this.numTasks = numTasks;
	}

	public int getNumTasks() {
		return numTasks;
	}

	public void setSimConfig(DynamicBreakdownSimConfig simConfig) {
		this.simConfig = simConfig;
	}

	public DynamicBreakdownSimConfig getSimConfig() {
		return simConfig;
	}

	public void setTasksForInds(final int[][] tasksForInds) {
		this.tasksForInds = tasksForInds;
	}

	public int[][] getTasksForInds() {
		return tasksForInds;
	}

	public void setIndsPerTask(List<Integer>[][] indsPerTask) {
		this.indsPerTask = indsPerTask;
	}

	public List<Integer>[][] getIndsPerTask() {
		return indsPerTask;
	}

}
