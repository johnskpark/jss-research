package app.evolution.multitask;

import java.util.List;

import app.simConfig.DynamicBreakdownSimConfig;
import ec.EvolutionState;
import ec.simple.SimpleEvolutionState;

public class MultitaskEvolutionState extends SimpleEvolutionState {

	private static final long serialVersionUID = 8005628891176535079L;

	public static final int NOT_SET = -1;

	private DynamicBreakdownSimConfig simConfig;
	private int numTasks;

	private int[][] numIndsPerTask;

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

	public void setNumIndsPerTask(final EvolutionState state, final int[][] numIndsPerTask) {
		this.numIndsPerTask = numIndsPerTask;
	}

	public int[][] getNumIndsPerTask() {
		return numIndsPerTask;
	}

	public void setIndsPerTask(List<Integer>[][] indsPerTask) {
		this.indsPerTask = indsPerTask;
	}

	public List<Integer>[][] getIndsPerTask() {
		return indsPerTask;
	}

}
