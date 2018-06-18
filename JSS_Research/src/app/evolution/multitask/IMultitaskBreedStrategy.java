package app.evolution.multitask;

public interface IMultitaskBreedStrategy {

	public int[][] getTasksForInds(MultitaskEvolutionState state, MultitaskBreeder breeder);

}
