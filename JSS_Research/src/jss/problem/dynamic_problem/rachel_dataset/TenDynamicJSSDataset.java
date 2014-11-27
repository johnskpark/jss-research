package jss.problem.dynamic_problem.rachel_dataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jss.IDataset;
import jss.IProblemInstance;
import jss.ProblemSize;
import jss.problem.dynamic_problem.DynamicInstance;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class TenDynamicJSSDataset implements IDataset {

	private int trainingIndex = 0;
	private int testingIndex = 1;

	private double[][] meanProcessingTimes = new double[][]{{25, 25}, {25, 50, 25, 50}};
	private double[][] utilisationRates = new double[][]{{0.85, 0.95}, {0.90, 0.90, 0.97, 0.97}};
	private double[][][] dueDateTightness = new double[][][]{
			{{3, 5, 7}, {3, 5, 7}},
			{{2, 4, 6}, {2, 4, 6}, {2, 4, 6}, {2, 4, 6}}
	};
	private int trainSize = 2;
	private int testSize = 4;

	private int[][][] processingOrderGenerators = new int[][][]{
			{{4, 4}, {8, 8}},
			{{4, 4}, {6, 6}, {8, 8}, {10, 10}, {2, 10}}
	};
	private int trainOperationOrdersSize = 2;
	private int testOperationOrdersSize = 5;

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

		generateDataset();
	}

	/**
	 * TODO javadoc.
	 * @param seed
	 */
	public TenDynamicJSSDataset(long s) {
		this.seed = s;
		this.rand = new Random(seed);

		generateDataset();
	}

	private void generateDataset() {
		generateTrainingSet();
		generateTestingSet();
	}

	private void generateTrainingSet() {
		for (int i = 0; i < trainSize; i++) {
			for (int j = 0; j < trainOperationOrdersSize; j++) {
				int minOperations = processingOrderGenerators[trainingIndex][i][0];
				int maxOperations = processingOrderGenerators[trainingIndex][i][1];

				double uniformMean = meanProcessingTimes[trainingIndex][i];
				double poissonMean = utilisationRates[trainingIndex][i] *
						meanProcessingTimes[trainingIndex][i] *
						(minOperations + maxOperations) / 2.0;

				double[] tightness = dueDateTightness[trainingIndex][i];

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
		for (int i = 0; i < testSize; i++) {
			for (int j = 0; j < testOperationOrdersSize; j++) {
				int minOperations = processingOrderGenerators[testingIndex][i][0];
				int maxOperations = processingOrderGenerators[testingIndex][i][1];

				double uniformMean = meanProcessingTimes[testingIndex][i];
				double poissonMean = utilisationRates[testingIndex][i] *
						meanProcessingTimes[testingIndex][i] *
						(minOperations + maxOperations) / 2.0;

				double[] tightness = dueDateTightness[testingIndex][i];

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
		DynamicInstance problemInstance = new DynamicInstance();

		problemInstance.setProcessingOrderGenerator(new VariableOperationNumberPOG(minOperations, maxOperations, rand.nextLong()));
		problemInstance.setProcessingTimeGenerator(new ProcessingTimeGenerator(uniformMean, rand.nextLong()));
		problemInstance.setJobReadyTimeGenerator(new JobReadyTimeGenerator(poissonMean, rand.nextLong()));
		problemInstance.setDueDateGenerator(new DueDateGenerator(tightness, rand.nextLong()));
		problemInstance.setPenaltyGenerator(new PenaltyGenerator(rand.nextLong()));

		return problemInstance;
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
	public List<IProblemInstance> getTest() {
		return testingSet;
	}

}
