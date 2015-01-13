package jss.problem.static_problem.taillard_dataset;

import java.util.List;
import java.util.Set;

import jss.IJob;
import jss.IMachine;
import jss.IProblemInstance;
import jss.problem.static_problem.StaticInstance;
import jss.problem.static_problem.StaticJob;

/**
 * Helper class that for calculating the lower bound for the Taillard's dataset.
 *
 * @author parkjohn
 *
 */
public class TaillardBounds {

	/**
	 * Calculate the upper bound for the specified JSS problem instance.
	 */
	public static double calculateUpperBound(StaticInstance problem) {
		throw new UnsupportedOperationException("Upper bound calculation on Taillard dataset not specified");
	}

	/**
	 * Calculate the lower bound for the specified JSS problem instance.
	 */
	public static double calculateLowerBound(StaticInstance problem) {
		Set<IMachine> machines = problem.getMachines();
		List<IJob> jobs = problem.getJobs();

		double[] b = getB(problem);
		double[] a = getA(problem);
		double[] T = getT(problem);

		double minMachines = Double.NEGATIVE_INFINITY;
		double minJobs = Double.NEGATIVE_INFINITY;

		for (int i = 0; i < machines.size(); i++) {
			double machine = b[i] + T[i] + a[i];
			minMachines = Math.max(machine, minMachines);
		}

		for (IJob job : jobs) {
			double sumProcessing = 0;
			for (IMachine machine : machines) {
				sumProcessing += job.getProcessingTime(machine);
			}
			minJobs = Math.max(sumProcessing, minJobs);
		}

		return Math.max(minMachines, minJobs);
	}

	// Calculation for the b_i values for Talliard's bound calculation.
	private static double[] getB(IProblemInstance problem) {
		Set<IMachine> machines = problem.getMachines();
		List<IJob> jobs = problem.getJobs();

		double[] b = new double[machines.size()];
		int i = 0;
		for (IMachine machine : machines) {
			b[i] = Double.POSITIVE_INFINITY;

			for (int j = 0; j < jobs.size(); j++) {
				StaticJob job = (StaticJob) jobs.get(j);

				int kPrime = -1;
				for (int k = 0; k < job.getNumOperations() && kPrime != -1; k++) {
					if (job.getMachine(k).equals(machine)) {
						kPrime = k;
					}
				}

				double sum = 0.0;

				for (int k = kPrime + 1; k < job.getNumOperations(); k++) {
					sum += job.getProcessingTime(k);
				}

				b[i] = Math.min(sum, b[i]);
			}
			i++;
		}

		return b;
	}

	// Calculation for the a_i values for Talliard's bound calculation.
	private static double[] getA(IProblemInstance problem) {
		Set<IMachine> machines = problem.getMachines();
		List<IJob> jobs = problem.getJobs();

		double[] a = new double[machines.size()];
		int i = 0;
		for (IMachine machine : machines) {
			a[i] = Double.POSITIVE_INFINITY;

			for (int j = 0; j < jobs.size(); j++) {
				StaticJob job = (StaticJob) jobs.get(j);

				int kPrime = -1;
				for (int k = 0; k < job.getNumOperations() && kPrime != -1; k++) {
					if (job.getMachine(k).equals(machine)) {
						kPrime = k;
					}
				}

				double sum = 0.0;

				for (int k = kPrime + 1; k < job.getNumOperations(); k++) {
					sum += job.getProcessingTime(k);
				}

				a[i] = Math.min(sum, a[i]);
			}
			i++;
		}

		return a;
	}

	// Calculation for the T_i values for Talliard's bound calculation.
	private static double[] getT(IProblemInstance problem) {
		Set<IMachine> machines = problem.getMachines();
		List<IJob> jobs = problem.getJobs();

		double[] T = new double[machines.size()];
		int i = 0;
		for (IMachine machine : machines) {
			T[i] = 0.0;

			for (IJob job : jobs) {
				T[i] += job.getProcessingTime(machine);
			}
			i++;
		}

		return T;
	}
}
