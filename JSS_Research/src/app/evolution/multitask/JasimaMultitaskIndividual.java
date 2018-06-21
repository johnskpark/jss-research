package app.evolution.multitask;

import java.util.Arrays;

import app.evolution.JasimaGPIndividual;
import ec.EvolutionState;

public class JasimaMultitaskIndividual extends JasimaGPIndividual {

	private static final long serialVersionUID = 2678166441642194373L;

	public static final int NOT_SET = -1;
	public static final int NO_TASK_SET = -1;

	private int task = NO_TASK_SET;
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


	@Override
    public void printIndividualForHumans(final EvolutionState state, final int log) {
        state.output.println(EVALUATED_PREAMBLE + (evaluated ? "true" : "false"), log);

        state.output.print("Standard ", log);
        fitness.printFitnessForHumans(state,log);

        if (task != NOT_SET) {
        	state.output.println("Task " + task + " Fitness: " + taskFitnesses[task], log);
        }

        state.output.print("Task ", log);
        printTrees(state,log);
	}

	@Override
	public Object clone() {
		JasimaMultitaskIndividual newObject = (JasimaMultitaskIndividual) super.clone();

		newObject.taskFitnesses = Arrays.copyOf(this.taskFitnesses, this.taskFitnesses.length);
		newObject.task = this.task;

		newObject.taskFitnesses = Arrays.copyOf(this.taskFitnesses, this.taskFitnesses.length);

		return newObject;
	}
}
