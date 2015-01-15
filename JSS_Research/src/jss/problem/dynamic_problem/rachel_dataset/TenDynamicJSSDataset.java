package jss.problem.dynamic_problem.rachel_dataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jss.IDataset;
import jss.IProblemInstance;
import jss.ProblemSize;
import jss.problem.dynamic_problem.DynamicInstance;
import jss.problem.dynamic_problem.DynamicMachine;

/**
 * Ten machine dynamic JSS problem that is used by Rachel for her evaluation
 * of the 'less-myopic' dispatching rule evolved using GP.
 *
 * @author parkjohn
 *
 */
public class TenDynamicJSSDataset implements IDataset {

	private static final int TRAINING_INDEX = 0;
	private static final int TESTING_INDEX = 1;

	private static final double[][] MEAN_PROCESSING_TIMES = new double[][]{{25, 25}, {25, 50, 25, 50}};
	private static final double[][] UTILISATION_RATES = new double[][]{{0.85, 0.95}, {0.90, 0.90, 0.97, 0.97}};
	private static final double[][][] DUE_DATE_TIGHTNESS = new double[][][]{
			{{3, 5, 7}, {3, 5, 7}},
			{{2, 4, 6}, {2, 4, 6}, {2, 4, 6}, {2, 4, 6}}
	};
	private static final int TRAINING_SIZE = 2;
	private static final int TESTING_SIZE = 4;

	private static final int[][][] PROCESSING_ORDER_GENERATORS = new int[][][]{
			{{4, 4}, {8, 8}},
			{{4, 4}, {6, 6}, {8, 8}, {10, 10}, {2, 10}}
	};
	private static final int TRAINING_OPERATION_ORDER_SIZE = 2;
	private static final int TESTING_OPERATION_ORDER_SIZE = 5;

	private static final int NUM_MACHINES = 10;

	private static final int WARM_UP_PERIOD = 500;

	private long seed;
	private Random rand;

	private List<DynamicInstance> problemInstances = new ArrayList<DynamicInstance>();

	private List<IProblemInstance> trainingSet = new ArrayList<IProblemInstance>();
	private List<IProblemInstance> testingSet = new ArrayList<IProblemInstance>();

	/**
	 * TODO javadoc.
	 */
	public TenDynamicJSSDataset() {
		this.seed = System.currentTimeMillis();
		this.rand = new Random(seed);
	}

	/**
	 * TODO javadoc.
	 * @param seed
	 */
	public TenDynamicJSSDataset(long s) {
		this.seed = s;
		this.rand = new Random(seed);
	}

	public void setSeed(long s) {
		this.seed = s;
		this.rand = new Random(seed);
	}

	public void generateDataset() {
		generateTrainingSet();
		generateTestingSet();
	}

	private void generateTrainingSet() {
		for (int i = 0; i < TRAINING_SIZE; i++) {
			for (int j = 0; j < TRAINING_OPERATION_ORDER_SIZE; j++) {
				int minOperations = PROCESSING_ORDER_GENERATORS[TRAINING_INDEX][i][0];
				int maxOperations = PROCESSING_ORDER_GENERATORS[TRAINING_INDEX][i][1];

				double uniformMean = MEAN_PROCESSING_TIMES[TRAINING_INDEX][i];
				double poissonMean = UTILISATION_RATES[TRAINING_INDEX][i] *
						MEAN_PROCESSING_TIMES[TRAINING_INDEX][i] *
						(minOperations + maxOperations) / (2.0 * NUM_MACHINES);

				double[] tightness = DUE_DATE_TIGHTNESS[TRAINING_INDEX][i];

				DynamicInstance problemInstance = generateProblemInstance(minOperations,
						maxOperations,
						uniformMean,
						poissonMean,
						tightness);

				problemInstances.add(problemInstance);
				trainingSet.add(problemInstance);
			}
		}
	}

	private void generateTestingSet() {
		for (int i = 0; i < TESTING_SIZE; i++) {
			for (int j = 0; j < TESTING_OPERATION_ORDER_SIZE; j++) {
				int minOperations = PROCESSING_ORDER_GENERATORS[TESTING_INDEX][i][0];
				int maxOperations = PROCESSING_ORDER_GENERATORS[TESTING_INDEX][i][1];

				double uniformMean = MEAN_PROCESSING_TIMES[TESTING_INDEX][i];
				double poissonMean = UTILISATION_RATES[TESTING_INDEX][i] *
						MEAN_PROCESSING_TIMES[TESTING_INDEX][i] *
						(minOperations + maxOperations) / (2.0 * NUM_MACHINES);

				double[] tightness = DUE_DATE_TIGHTNESS[TESTING_INDEX][i];

				DynamicInstance problemInstance = generateProblemInstance(minOperations,
						maxOperations,
						uniformMean,
						poissonMean,
						tightness);

				problemInstances.add(problemInstance);
				testingSet.add(problemInstance);
			}
		}
	}

	private DynamicInstance generateProblemInstance(int minOperations,
			int maxOperations,
			double uniformMean,
			double poissonMean,
			double[] tightness) {
		DynamicInstance problem = new DynamicInstance();

		addMachines(problem);
		addProcessingOrderGenerator(problem, minOperations, maxOperations);
		addProcessingTimeGenerator(problem, uniformMean);
		addJobReadyTimeGenerator(problem, poissonMean);
		addDueDateGenerator(problem, tightness);
		addPenaltyGenerator(problem);
		addTerminationCriterion(problem);
		setWarmUp(problem);

		return problem;
	}

	// Add in the different components of a problem instance.

	private void addMachines(DynamicInstance problemInstance) {
		for (int i = 0; i < NUM_MACHINES; i++) {
			problemInstance.addMachine(new DynamicMachine(i, problemInstance));
		}
	}

	private void addProcessingOrderGenerator(DynamicInstance problemInstance, int minOperations, int maxOperations) {
		problemInstance.setProcessingOrderGenerator(new
				VariableOperationNumberPOG(problemInstance.getMachines(), minOperations, maxOperations, rand.nextLong()));
	}

	private void addProcessingTimeGenerator(DynamicInstance problemInstance, double uniformMean) {
		problemInstance.setProcessingTimeGenerator(new
				ProcessingTimeGenerator(uniformMean, rand.nextLong()));
	}

	private void addJobReadyTimeGenerator(DynamicInstance problemInstance, double poissonMean) {
		problemInstance.setJobReadyTimeGenerator(new
				JobReadyTimeGenerator(poissonMean, rand.nextLong()));
	}

	private void addDueDateGenerator(DynamicInstance problemInstance, double[] tightness) {
		problemInstance.setDueDateGenerator(new
				DueDateGenerator(tightness, rand.nextLong()));
	}

	private void addPenaltyGenerator(DynamicInstance problemInstance) {
		problemInstance.setPenaltyGenerator(new PenaltyGenerator(rand.nextLong()));
	}

	private void addTerminationCriterion(DynamicInstance problemInstance) {
		problemInstance.setTerminationCriterion(new TerminationCriterion(problemInstance));
	}

	private void setWarmUp(DynamicInstance problemInstance) {
		problemInstance.setWarmUp(WARM_UP_PERIOD);
	}

	@Override
	public List<IProblemInstance> getProblems() {
		return new ArrayList<IProblemInstance>(problemInstances);
	}

	@Override
	public List<IProblemInstance> getTraining(ProblemSize problemSize) {
		return trainingSet;
	}

	@Override
	public List<IProblemInstance> getTesting() {
		return testingSet;
	}

}
