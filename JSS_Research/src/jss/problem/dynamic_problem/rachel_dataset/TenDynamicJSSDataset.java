package jss.problem.dynamic_problem.rachel_dataset;

import java.util.List;
import java.util.Random;

import jss.IDataset;
import jss.IProblemInstance;
import jss.ProblemSize;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class TenDynamicJSSDataset implements IDataset {

	private long seed;
	private Random rand;

	/**
	 * TODO javadoc.
	 */
	public TenDynamicJSSDataset() {
		this.seed = System.currentTimeMillis();
		this.rand = new Random();
	}

	/**
	 * TODO javadoc.
	 * @param seed
	 */
	public TenDynamicJSSDataset(long seed) {
		this.seed = seed;
		this.rand = new Random(seed);
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
