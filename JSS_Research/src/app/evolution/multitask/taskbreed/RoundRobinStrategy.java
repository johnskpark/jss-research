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

	    	int indsPerTaskFloor = subpopSize / numTasks;
	    	int remainderInds = subpopSize - indsPerTaskFloor * numTasks;

    		tasksForInds[s] = new int[subpopSize];

	    	int task = 0;
	    	int indTracker = indsPerTaskFloor + (((remainderInds - task) != 0) ? 1 : 0);
	    	for (int i = 0; i < subpopSize; i++) {
	    		tasksForInds[s][i] = task;

	    		indTracker--;
	    		if (indTracker == 0) {
	    			indTracker = indsPerTaskFloor + (((remainderInds - task) != 0) ? 1 : 0);
	    			task++;
	    		}
	    	}
    	}

    	return tasksForInds;
	}

}
