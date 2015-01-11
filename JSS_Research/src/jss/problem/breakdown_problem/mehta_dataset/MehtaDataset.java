package jss.problem.breakdown_problem.mehta_dataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jss.IDataset;
import jss.IProblemInstance;
import jss.ProblemSize;
import jss.problem.static_problem.StaticInstance;

/**
 * TODO javadoc. I want this to either be dynamic or static.
 *
 * @author John Park
 *
 */
public class MehtaDataset implements IDataset {

	private static final int[] NUM_JOBS = new int[]{10, 20, 30};
	private static final int[] NUM_MACHINES = new int[]{6, 10};

	private static final double[] TARDINESS = new double[]{0.3, 0.6};
	private static final double[] DUE_DATE = new double[]{0.5, 2.5};

	private static final int MIN_PROCESSING_TIME = 4;
	private static final int MAX_PROCESSING_TIME = 8;

	// TODO placeholder.
	private static final int TRAINING_SMALL_MACHINE_NUM = 15;
	private static final int TRAINING_SMALL_JOB_NUM = 15;
	private static final int TRAINING_MEDIUM_MACHINE_NUM = 20;
	private static final int TRAINING_MEDIUM_JOB_NUM = 30;
	private static final int TRAINING_LARGE_MACHINE_NUM = 20;
	private static final int TRAINING_LARGE_JOB_NUM = 100;

	private long seed;
	private Random rand;

	private List<StaticInstance> problemInstances = new ArrayList<StaticInstance>();

	private List<IProblemInstance> smallInstances = new ArrayList<IProblemInstance>();
	private List<IProblemInstance> mediumInstances = new ArrayList<IProblemInstance>();
	private List<IProblemInstance> largeInstances = new ArrayList<IProblemInstance>();

	/**
	 * TODO javadoc.
	 */
	public MehtaDataset() {
		seed = System.currentTimeMillis();
		rand = new Random(seed);
	}

	/**
	 * TODO javadoc.
	 * @param s
	 */
	public MehtaDataset(long s) {
		seed = s;
		rand = new Random(seed);
	}

	public void setSeed(long s) {
		seed = s;
		rand = new Random(seed);
	}

	public void generateDataset() {
		// Convert from raw .csv file.
		generateDataset();

		// Generate the lists of the training sets.
		generateTrainingSets();
	}

	private void generateTrainingSets() {
		for (IProblemInstance problem : problemInstances) {
			int machineSize = problem.getMachines().size();
			int jobSize = problem.getJobs().size();

			if (machineSize == TRAINING_SMALL_MACHINE_NUM && jobSize == TRAINING_SMALL_JOB_NUM) {
				smallInstances.add(problem);
			} else if (machineSize == TRAINING_MEDIUM_MACHINE_NUM && jobSize == TRAINING_MEDIUM_JOB_NUM) {
				mediumInstances.add(problem);
			} else if (machineSize == TRAINING_LARGE_MACHINE_NUM && jobSize == TRAINING_LARGE_JOB_NUM) {
				largeInstances.add(problem);
			}
		}

		int smallSize = smallInstances.size() / 2;
		for (int i = 0; i < smallSize; i++) {
			smallInstances.remove(smallInstances.size() - 1);
		}

		int mediumSize = mediumInstances.size() / 2;
		for (int i = 0; i < mediumSize; i++) {
			mediumInstances.remove(mediumInstances.size() - 1);
		}

		int largeSize = largeInstances.size() / 2;
		for (int i = 0; i < largeSize; i++) {
			largeInstances.remove(largeInstances.size() - 1);
		}
	}

	@Override
	public List<IProblemInstance> getProblems() {
		return new ArrayList<IProblemInstance>(problemInstances);
	}

	@Override
	public List<IProblemInstance> getTraining(ProblemSize problemSize) {
		switch (problemSize) {
		case SMALL_PROBLEM_SIZE: return smallInstances;
		case MEDIUM_PROBLEM_SIZE: return mediumInstances;
		case LARGE_PROBLEM_SIZE: return largeInstances;
		default: throw new RuntimeException("You done goofed");
		}
	}

	@Override
	public List<IProblemInstance> getTest() {
		return new ArrayList<IProblemInstance>(problemInstances);
	}

}
