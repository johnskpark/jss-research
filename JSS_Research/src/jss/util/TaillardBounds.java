package jss.util;

import java.util.List;

import jss.IJob;
import jss.IMachine;
import jss.IProblemInstance;
import jss.problem.static_problem.StaticInstance;
import jss.problem.static_problem.StaticJob;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class TaillardBounds {

	/**
	 * Calculate the upper bound of the JSS problem instance.
	 * @param problem TODO
	 * @return
	 */
	public static double calculateUpperBound(StaticInstance problem) {
		List<IMachine> machines = problem.getMachines();
		List<IJob> jobs = problem.getJobs();

		double[] b = getB(problem);
		double[] a = getA(problem);
		double[] T = getT(problem);

		// TODO
		return 0;
	}

	/**
	 * Calculate the lower bound of the JSS problem instance.
	 * @param problem TODO
	 * @return
	 */
	public static double calculateLowerBound(StaticInstance problem) {
		List<IMachine> machines = problem.getMachines();
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
		List<IMachine> machines = problem.getMachines();
		List<IJob> jobs = problem.getJobs();

		double[] b = new double[machines.size()];
		for (int i = 0; i < machines.size(); i++) {
			IMachine machine = machines.get(i);

			b[i] = Double.POSITIVE_INFINITY;

			for (int j = 0; j < jobs.size(); j++) {
				StaticJob job = (StaticJob) jobs.get(j);
				List<IMachine> orderedMachines = job.getProcessingOrder();

				double sum = 0.0;
				int kPrime = orderedMachines.indexOf(machine);
				System.out.printf("b: %d,%d,%d\t", i, j, kPrime);
				for (int k = 0; k < kPrime - 1; k++) {
					sum += job.getProcessingTime(orderedMachines.get(k));
				}

				b[i] = Math.min(sum, b[i]);
			}
		}
		System.out.println();

		return b;
	}

	// Calculation for the a_i values for Talliard's bound calculation.
	private static double[] getA(IProblemInstance problem) {
		List<IMachine> machines = problem.getMachines();
		List<IJob> jobs = problem.getJobs();

		double[] a = new double[machines.size()];
		for (int i = 0; i < machines.size(); i++) {
			IMachine machine = machines.get(i);

			a[i] = Double.POSITIVE_INFINITY;

			for (int j = 0; j < jobs.size(); j++) {
				StaticJob job = (StaticJob) jobs.get(j);
				List<IMachine> orderedMachines = job.getProcessingOrder();

				double sum = 0.0;
				int kPrime = orderedMachines.indexOf(machine);
				System.out.printf("a: %d,%d,%d\t", i, j, kPrime);
				for (int k = kPrime + 1; k < machines.size(); k++) {
					sum += job.getProcessingTime(orderedMachines.get(k));
				}

				a[i] = Math.min(sum, a[i]);
			}
		}
		System.out.println();

		return a;
	}

	// Calculation for the T_i values for Talliard's bound calculation.
	private static double[] getT(IProblemInstance problem) {
		List<IMachine> machines = problem.getMachines();
		List<IJob> jobs = problem.getJobs();

		double[] T = new double[machines.size()];
		for (int i = 0; i < machines.size(); i++) {
			T[i] = 0.0;

			for (IJob job : jobs) {
				T[i] += job.getProcessingTime(machines.get(i));
			}
		}

		return T;
	}
}
