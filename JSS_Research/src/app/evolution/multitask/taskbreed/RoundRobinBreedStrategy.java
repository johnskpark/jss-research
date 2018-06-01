package app.evolution.multitask.taskbreed;

import app.evolution.multitask.IMultitaskBreedStrategy;
import app.evolution.multitask.MultitaskEvolutionState;
import ec.Subpopulation;

public class RoundRobinBreedStrategy implements IMultitaskBreedStrategy {

	@Override
	public int[][] getNumIndsPerTask(MultitaskEvolutionState state) {
    	Subpopulation[] subpops = state.population.subpops;

    	int numTasks = state.getNumTasks();
    	int[][] numIndsPerTask = new int[subpops.length][numTasks];

    	for (int s = 0; s < subpops.length; s++) {
	    	int popSize = subpops[s].individuals.length;
	    	int indsPerTaskFloor = popSize / numTasks;
	    	int leftoverInds = popSize - indsPerTaskFloor * numTasks;

	    	for (int t = 0; t < numTasks; t++) {
	    		int numInds = indsPerTaskFloor + ((t < leftoverInds) ? 1 : 0);

	    		if (t == 0) {
	    			numIndsPerTask[s][t] = numInds;
	    		} else {
	    			numIndsPerTask[s][t] = numIndsPerTask[s][t - 1] + numInds;
	    		}
	    	}
    	}

    	return numIndsPerTask;
	}

}
