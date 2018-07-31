package app.evolution.multitask;

import java.util.ArrayList;
import java.util.List;

import ec.Fitness;
import ec.gp.koza.KozaFitness;

public class MultitaskKozaFitness extends KozaFitness {

	private static final long serialVersionUID = -9189815787344424531L;

	public static final int NOT_SET = -1;

	private List<Double> taskFitnesses = new ArrayList<Double>();
	private int numTasks;

	public void setNumTasks(int numTasks) {
		this.numTasks = numTasks;
		this.taskFitnesses = new ArrayList<Double>(numTasks);

		for (int i = 0; i < numTasks; i++) {
			this.taskFitnesses.add((double) NOT_SET);
		}
	}

	public int getNumTasks() {
		return numTasks;
	}

	public void setTaskFitness(int taskIndex, double fitness) {
		taskFitnesses.set(taskIndex, fitness);
	}

	public List<Double> getTaskFitnesses() {
		return taskFitnesses;
	}

	public double getTaskFitness(int taskIndex) {
		return taskFitnesses.get(taskIndex);
	}

	@Override
	public boolean equivalentTo(final Fitness _fitness) {
		// Compare each task fitnesses against each other.
		MultitaskKozaFitness other = (MultitaskKozaFitness) _fitness;

		boolean matching = true;
		for (int i = 0; i < numTasks && matching; i++) {
			matching = (this.getTaskFitness(i) == other.getTaskFitness(i));
		}
		return matching;
    }

	@Override
	public boolean betterThan(final Fitness _fitness) {
	    // Compare the number of task fitnesses and the overall fitness.
	    // If the number of task fitness is higher, then the individual is better.
	    // Otherwise, its the standard Koza fitness comparison.
		MultitaskKozaFitness other = (MultitaskKozaFitness) _fitness;
	    int thisCount = 0, otherCount = 0;
	    for (int i = 0; i < numTasks; i++) {
	    	if (this.getTaskFitness(i) != NOT_SET) { thisCount++; }
	    	if (other.getTaskFitness(i) != NOT_SET) { otherCount++; }
	    }

	    return (thisCount > otherCount) ||
	    		(thisCount == otherCount && other.standardizedFitness > this.standardizedFitness());
    }

	@Override
	public Object clone() {
		MultitaskKozaFitness newObject = (MultitaskKozaFitness) super.clone();

		newObject.taskFitnesses = new ArrayList<Double>(this.taskFitnesses);

		return newObject;
	}

}
