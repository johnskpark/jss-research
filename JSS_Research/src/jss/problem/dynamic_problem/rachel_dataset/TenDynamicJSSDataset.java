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

	private long seed;
	private Random rand;

	private double[] meanProcessingTime = new double[]{25, 25, 25, 50, 25, 50};
	private double[] utilisation = new double[]{0.85, 0.95, 0.90, 0.90, 0.97, 0.97};
	private double[][] dueDateTightness = new double[][]{{3, 5, 7}, {3, 5, 7}, {2, 4, 6},
			{2, 4, 6}, {2, 4, 6}, {2, 4, 6}};

	private List<DynamicInstance> problemInstances = new ArrayList<DynamicInstance>();

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

	private void generateDataset() {
		// TODO placeholder.
	}

	@Override
	public List<IProblemInstance> getProblems() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IProblemInstance> getTraining(ProblemSize problemSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IProblemInstance> getTest() {
		// TODO Auto-generated method stub
		return null;
	}

}
