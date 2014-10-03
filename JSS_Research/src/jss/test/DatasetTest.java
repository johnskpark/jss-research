package jss.test;

import java.util.List;

import jss.IJob;
import jss.IMachine;
import jss.IProblemInstance;
import jss.problem.static_problem.talliard_dataset.TalliardDataset;

import org.junit.Assert;
import org.junit.Test;

/**
 * TODO integration test.
 *
 * @author parkjohn
 *
 */
public class DatasetTest {

	@Test
	public void talliardDatasetTest_TestGeneration() {
		try {
			TalliardDataset dataset = new TalliardDataset();

			List<IProblemInstance> problems = dataset.getProblems();
			List<Double> upperBounds = dataset.getUpperBounds();
			List<Double> lowerBounds = dataset.getLowerBounds();

			Assert.assertEquals(problems.size(), upperBounds.size());
			Assert.assertEquals(problems.size(), lowerBounds.size());

			for (int i = 0; i < problems.size(); i++) {
				talliardInstanceTest(problems.get(i),
						upperBounds.get(i),
						lowerBounds.get(i));
			}

		} catch (RuntimeException ex) {
			Assert.fail();
		}
	}

	// TODO doc.
	private void talliardInstanceTest(IProblemInstance problem,
			double expectedUB,
			double expectedLB) {
		double[] b = getB(problem);
		double[] a = getA(problem);
		double[] T = getT(problem);

		double actualLB = 0;
	}

	// TODO doc.
	private double[] getB(IProblemInstance problem) {
		List<IMachine> machines = problem.getMachines();
		List<IJob> jobs = problem.getJobs();

		double[] b = new double[machines.size()];
		for (int i = 0; i < machines.size(); i++) {
			b[i] = Double.POSITIVE_INFINITY;

			for (int j = 0; j < jobs.size(); j++) {
				double sum = 0.0;
				int kPrime = machines.size(); // TODO placeholder
				for (int k = 0; k < kPrime; k++) {
					sum += jobs.get(j).getProcessingTime(machines.get(k));
				}

				if (sum < b[i]) {
					b[i] = sum;
				}
			}
		}

		return b;
	}

	// TODO doc.
	private double[] getA(IProblemInstance problem) {
		List<IMachine> machines = problem.getMachines();
		List<IJob> jobs = problem.getJobs();

		double[] a = new double[machines.size()];
		for (int i = 0; i < machines.size(); i++) {
			a[i] = Double.POSITIVE_INFINITY;

			for (int j = 0; j < jobs.size(); j++) {
				double sum = 0.0;
				int kPrime = machines.size(); // TODO placeholder
				for (int k = 0; k < kPrime; k++) {
					sum += jobs.get(j).getProcessingTime(machines.get(k));
				}

				if (sum < a[i]) {
					a[i] = sum;
				}
			}
		}

		return a;
	}

	// TODO doc.
	private double[] getT(IProblemInstance problem) {
		List<IMachine> machines = problem.getMachines();
		List<IJob> jobs = problem.getJobs();

		double[] T = new double[machines.size()];
		for (int i = 0; i < machines.size(); i++) {
			T[i] = 0.0;

			for (int j = 0; j < jobs.size(); j++) {
				T[i] += jobs.get(j).getProcessingTime(machines.get(i));
			}
		}

		return T;
	}
}
