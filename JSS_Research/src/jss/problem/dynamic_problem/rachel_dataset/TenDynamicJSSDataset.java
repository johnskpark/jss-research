package jss.problem.dynamic_problem.rachel_dataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jss.IDataset;
import jss.IProblemInstance;
import jss.ProblemSize;
import jss.problem.dynamic_problem.DynamicInstance;
import jss.problem.dynamic_problem.IProcessingOrderGenerator;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class TenDynamicJSSDataset implements IDataset {

	private long seed;
	private Random rand;

	private double[][] meanProcessingTimes = new double[][]{{25, 25}, {25, 50, 25, 50}};
	private double[][] utilisationRates = new double[][]{{0.85, 0.95}, {0.90, 0.90, 0.97, 0.97}};
	private double[][][] dueDateTightness = new double[][][]{
			{{3, 5, 7}, {3, 5, 7}},
			{{2, 4, 6}, {2, 4, 6}, {2, 4, 6}, {2, 4, 6}}
	};

	private IProcessingOrderGenerator[][] processingOrderGenerators = new IProcessingOrderGenerator[][]{
		{
			new FixedOperationNumberPOG(4),
			new FixedOperationNumberPOG(8)
		},
		{
			new FixedOperationNumberPOG(4),
			new FixedOperationNumberPOG(6),
			new FixedOperationNumberPOG(8),
			new FixedOperationNumberPOG(10),
			new VariableOperationNumberPOG(2, 10)
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
		// TODO placeholder.
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
