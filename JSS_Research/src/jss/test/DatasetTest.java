package jss.test;

import java.util.List;
import java.util.Set;

import jss.IJob;
import jss.IMachine;
import jss.IProblemInstance;
import jss.ProblemSize;
import jss.problem.static_problem.StaticInstance;
import jss.problem.static_problem.StaticJob;
import jss.problem.static_problem.StaticMachine;
import jss.problem.static_problem.taillard_dataset.TaillardBounds;
import jss.problem.static_problem.taillard_dataset.TaillardDataset;

import org.junit.Assert;
import org.junit.Test;

/**
 * TODO integration test. Currently tests the Taillard dataset.
 *
 * @author parkjohn
 *
 */
public class DatasetTest {

	private static final double EPSILON = 0.001;

	@Test
	public void taillardDatasetTest_TestFirstProblemInstance() {
		double[][] processingTimes = new double[][]
				{{94,66,10,53,26,15,65,82,10,27,93,92,96,70,83},
				{74,31,88,51,57,78,8,7,91,79,18,51,18,99,33},
				{4,82,40,86,50,54,21,6,54,68,82,20,39,35,68},
				{73,23,30,30,53,94,58,93,32,91,30,56,27,92,9},
				{78,23,21,60,36,29,95,99,79,76,93,42,52,42,96},
				{29,61,88,70,16,31,65,83,78,26,50,87,62,14,30},
				{18,75,20,4,91,68,19,54,85,73,43,24,37,87,66},
				{32,52,9,49,61,35,99,62,6,62,7,80,3,57,7},
				{85,30,96,91,13,87,82,83,78,56,85,8,66,88,15},
				{5,59,30,60,41,17,66,89,78,88,69,45,82,6,13},
				{90,27,1,8,91,80,89,49,32,28,90,93,6,35,73},
				{47,43,75,8,51,3,84,34,28,60,69,45,67,58,87},
				{65,62,97,20,31,33,33,77,50,80,48,90,75,96,44},
				{28,21,51,75,17,89,59,56,63,18,17,30,16,7,35},
				{57,16,42,34,37,26,68,73,5,8,12,87,83,20,97}};

		int[][] processingOrders = new int[][]
				{{7,13,5,8,4,3,11,12,9,15,10,14,6,1,2},
				{5,6,8,15,14,9,12,10,7,11,1,4,13,2,3},
				{2,9,10,13,7,12,14,6,1,3,8,11,5,4,15},
				{6,3,10,7,11,1,14,5,8,15,12,9,13,2,4},
				{8,9,7,11,5,10,3,15,13,6,2,14,12,1,4},
				{6,4,13,14,12,5,15,8,3,2,11,1,10,7,9},
				{13,4,8,9,15,7,2,12,5,6,3,11,1,14,10},
				{12,6,1,8,13,14,15,2,3,9,5,4,10,7,11},
				{11,12,7,15,1,2,3,6,13,5,9,8,10,14,4},
				{7,12,10,3,9,1,14,4,11,8,2,13,15,5,6},
				{5,8,14,1,6,13,7,9,15,11,4,2,12,10,3},
				{3,15,1,13,7,11,8,6,9,10,14,2,4,12,5},
				{6,9,11,3,4,7,10,1,14,5,2,12,13,8,15},
				{9,15,5,14,6,7,10,2,13,8,12,11,4,3,1},
				{11,9,13,7,5,2,14,15,12,1,8,4,3,10,6}};

		try {
			TaillardDataset dataset = new TaillardDataset();
			dataset.generateDataset();

			List<IProblemInstance> problems = dataset.getProblems();

			Assert.assertFalse(problems.isEmpty());
			Assert.assertTrue(problems.get(0) instanceof StaticInstance);

			StaticInstance problem = (StaticInstance) problems.get(0);

			Set<? extends IMachine> machines = problem.getMachines();
			List<? extends IJob> jobs = problem.getJobs();

			for (int j = 0; j < jobs.size(); j++) {
				Assert.assertTrue(jobs.get(j) instanceof StaticJob);
				StaticJob job = (StaticJob) jobs.get(j);

				for (int i = 0; i < job.getNumOperations(); i++) {
					IMachine machine = job.getMachine(i);

					Assert.assertTrue(machine instanceof StaticMachine);
					Assert.assertEquals(job.getProcessingTime(machine), processingTimes[j][i], EPSILON);
					// Assert.assertEquals(machine, machines.get(processingOrders[j][i]-1));
				}
			}

		} catch (RuntimeException ex) {
			Assert.fail();
		}
	}

	@Test
	public void taillardDatasetTest_TestUpperBound() {
		try {
			TaillardDataset dataset = new TaillardDataset();

			List<IProblemInstance> problems = dataset.getProblems();

			for (int i = 0; i < problems.size(); i++) {
				taillardInstanceTest(problems.get(i));
			}

		} catch (RuntimeException ex) {
			Assert.fail();
		}
	}

	// Test each problem instance whether their upper and lower bounds
	// meet the one expected by the benchmark.
	private void taillardInstanceTest(IProblemInstance problem) {
		StaticInstance staticProblem = (StaticInstance) problem;

		//double actualUB = TalliardBounds.calculateUpperBound(staticProblem);
		double actualLB = TaillardBounds.calculateLowerBound(staticProblem);

		// Assert.assertEquals(staticProblem.getUpperBound(), actualUB, EPSILON);
		Assert.assertEquals(staticProblem.getLowerBound(), actualLB, EPSILON);
	}

	@Test
	public void problemSizeTest_StringToEnum() {
		String smallSizeStr = "small";
		ProblemSize smallSizeEnum = ProblemSize.strToProblemSize(smallSizeStr);
		Assert.assertEquals(ProblemSize.SMALL_PROBLEM_SIZE, smallSizeEnum);

		String mediumSizeStr = "medium";
		ProblemSize mediumSizeEnum = ProblemSize.strToProblemSize(mediumSizeStr);
		Assert.assertEquals(ProblemSize.MEDIUM_PROBLEM_SIZE, mediumSizeEnum);

		String largeSizeStr = "large";
		ProblemSize largeSizeEnum = ProblemSize.strToProblemSize(largeSizeStr);
		Assert.assertEquals(ProblemSize.LARGE_PROBLEM_SIZE, largeSizeEnum);
	}

}
