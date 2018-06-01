package app.evolution.multitask;

import java.util.Arrays;

import app.evolution.JasimaGPIndividual;

public class JasimaMultitaskIndividual extends JasimaGPIndividual {

	private static final long serialVersionUID = 2678166441642194373L;

	public static final int NOT_SET = -1;

	private int task;
	private double[] taskFitnesses;

	public void setAssignedTask(int task) {
		this.task = task;
	}

	public int getAssignedTask() {
		return task;
	}

	public void setNumTasks(int numTasks) {
		taskFitnesses = new double[numTasks];

		Arrays.fill(taskFitnesses, NOT_SET);
	}

	public int getNumTasks() {
		return taskFitnesses.length;
	}

	public void setTaskFitness(int taskIndex, double fitness) {
		taskFitnesses[taskIndex] = fitness;
	}

	public double getTaskFitness(int taskIndex) {
		return taskFitnesses[taskIndex];
	}

}
