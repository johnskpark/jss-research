package jss;

import java.util.List;

/**
 * Represents a repository of problem instances for Job Shop Scheduling.
 *
 * @author parkjohn
 *
 */
public interface IDataset {

	/**
	 * Set the seed for generating the dataset, if the dataset is generated
	 * procedurally.
	 * @param s the seed value.
	 */
	public void setSeed(long s);

	/**
	 * Generate the dataset.
	 */
	public void generateDataset();

	/**
	 * Get the list of Job Shop Scheduling problem instances stored in the
	 * dataset.
	 */
	public List<IProblemInstance> getProblems();

	/**
	 * Get a partial list of the problem instances for a training set that can
	 * be used for training any machine learning techniques.
	 * @param problemSize Specifies the size and complexity of the problem
	 *                    instances that should be extracted for the training
	 *                    set.
	 */
	public List<IProblemInstance> getTraining(ProblemSize problemSize);

	/**
	 * Get a partial list of the problem instances for a test set that can be
	 * used for testing the trained model.
	 */
	public List<IProblemInstance> getTesting();

}
