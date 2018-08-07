package app.evolution.multitask;

import java.util.List;

import app.evolution.JasimaGPIndividual;
import ec.EvolutionState;

public class JasimaMultitaskIndividual extends JasimaGPIndividual {

	private static final long serialVersionUID = 2678166441642194373L;

	public static final int NO_TASK_SET = -1;

	private int assignedTask = NO_TASK_SET;

	public void setAssignedTask(int task) {
		this.assignedTask = task;
	}

	public int getAssignedTask() {
		return assignedTask;
	}

	public void setNumTasks(int numTasks) {
		((MultitaskKozaFitness) getFitness()).setNumTasks(numTasks);
	}

	public int getNumTasks() {
		return ((MultitaskKozaFitness) getFitness()).getNumTasks();
	}

	public void setTaskFitness(int taskIndex, double fitness) {
		((MultitaskKozaFitness) getFitness()).setTaskFitness(taskIndex, fitness);
	}

	public double getTaskFitness(int taskIndex) {
		return ((MultitaskKozaFitness) getFitness()).getTaskFitness(taskIndex);
	}

	public List<Double> getTaskFitnesses() {
		return ((MultitaskKozaFitness) getFitness()).getTaskFitnesses();
	}

	public boolean taskFitnessBetterThan(JasimaMultitaskIndividual other, int task) {
		if (this.getTaskFitness(task) < other.getTaskFitness(task) ||
				(this.getTaskFitness(task) == other.getTaskFitness(task) &&
				this.getFitness().betterThan(other.getFitness()))) {
			return true;
		} else {
			return false;
		}
	}

	@Override
    public void printIndividualForHumans(final EvolutionState state, final int log) {
		MultitaskKozaFitness fitness = (MultitaskKozaFitness) getFitness();

        state.output.println(EVALUATED_PREAMBLE + (evaluated ? "true" : "false"), log);

        state.output.print("Standard ", log);
        fitness.printFitnessForHumans(state,log);

        state.output.println("Assigned task: " + ((assignedTask != NO_TASK_SET) ? assignedTask : "NOT_SET") + ", fitnesses: " + fitness.getTaskFitnesses(), log);

        state.output.print("Task ", log);
        printTrees(state,log);
	}

}
