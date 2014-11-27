package jss.problem.dynamic_problem.rachel_dataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jss.IDataset;
import jss.IProblemInstance;
import jss.ProblemSize;
import jss.problem.dynamic_problem.DynamicInstance;
import jss.problem.dynamic_problem.IDoubleValueGenerator;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class TenDynamicJSSDataset implements IDataset {

	private static final int FIXED_OPERATION_NUMBER = 0;
	private static final int VARIABLE_OPERATION_NUMBER = 1;

	private long seed;
	private Random rand;

	private double[][] meanProcessingTimes = new double[][]{{25, 25}, {25, 50, 25, 50}};
	private double[][] utilisationRates = new double[][]{{0.85, 0.95}, {0.90, 0.90, 0.97, 0.97}};
	private double[][][] dueDateTightness = new double[][][]{
			{{3, 5, 7}, {3, 5, 7}},
			{{2, 4, 6}, {2, 4, 6}, {2, 4, 6}, {2, 4, 6}}
	};
	private int trainSize = 2;
	private int testSize = 4;

	private int[][][] processingOrderGenerators = new int[][][]{
			{
				{FIXED_OPERATION_NUMBER, 4},
				{FIXED_OPERATION_NUMBER, 8}
			},
			{
				{FIXED_OPERATION_NUMBER, 4},
				{FIXED_OPERATION_NUMBER, 6},
				{FIXED_OPERATION_NUMBER, 8},
				{FIXED_OPERATION_NUMBER, 10},
				{VARIABLE_OPERATION_NUMBER, 2, 10}
			}
	};

	private List<DynamicInstance> problemInstances = new ArrayList<DynamicInstance>();

	private List<IProblemInstance> trainingSet = new ArrayList<IProblemInstance>();
	private List<IProblemInstance> testSet = new ArrayList<IProblemInstance>();

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
		for (int i = 0; i < trainSize; i++) {
			for (int j = 0; j < processingOrderGenerators[0].length; j++) {
				DynamicInstance problemInstance = new DynamicInstance();

				problemInstance.setProcessingOrderGenerator(null); // TODO

				problemInstance.setProcessingTimeGenerator(new ProcessingTimeGenerator(meanProcessingTimes[0][i], rand.nextLong()));

				problemInstance.setJobReadyTimeGenerator(null); // TODO
				problemInstance.setDueDateGenerator(null); // TODO
				problemInstance.setPenaltyGenerator(null); // TODO
			}
		}
	}
	
	private void generateProblemInstance() {
		
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
		return testSet;
	}

}
