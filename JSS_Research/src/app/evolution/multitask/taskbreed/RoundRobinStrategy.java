package app.evolution.multitask.taskbreed;

import app.evolution.multitask.IMultitaskBreedStrategy;
import app.evolution.multitask.MultitaskBreeder;
import app.evolution.multitask.MultitaskEvolutionState;
import ec.Subpopulation;

public class RoundRobinStrategy implements IMultitaskBreedStrategy {

	@Override
	public int[][] getTasksForInds(MultitaskEvolutionState state, MultitaskBreeder breeder) {
    	Subpopulation[] subpops = state.population.subpops;

    	int numTasks = state.getNumTasks();
    	int[][] tasksForInds = new int[subpops.length][];

    	for (int s = 0; s < subpops.length; s++) {
    		int subpopSize = subpops[s].individuals.length - breeder.numElites(state, s);

	    	int numIndsPerTask = subpopSize / numTasks;
	    	int remainderInds = subpopSize - numIndsPerTask * numTasks;

    		tasksForInds[s] = new int[subpopSize];

    		int taskCount = 0;
	    	int taskIndex = 0;
	    	for (int i = 0; i < subpopSize; i++) {
	    		tasksForInds[s][i] = taskIndex;

	    		taskCount++;
	    		if (taskCount >= numIndsPerTask + ((taskIndex < remainderInds) ? 1 : 0)) {
	    			taskCount = 0;
	    			taskIndex++;
	    		}
	    	}
    	}

    	return tasksForInds;
	}

}
