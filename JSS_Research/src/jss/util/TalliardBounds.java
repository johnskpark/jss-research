package jss.util;

import java.util.List;

import jss.IJob;
import jss.IMachine;
import jss.IProblemInstance;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class TalliardBounds {

	/**
	 * TODO javadoc.
	 * @param problem
	 * @return
	 */
	public static double calculateUpperBound(IProblemInstance problem) {
		List<IMachine> machines = problem.getMachines();
		List<IJob> jobs = problem.getJobs();

		double[] b = getB(problem);
		double[] a = getA(problem);
		double[] T = getT(problem);

		// TODO
		return 0;
	}

	/**
	 * TODO javadoc.
	 * @param problem
	 * @return
	 */
	public static double calculateLowerBound(IProblemInstance problem) {
		List<IMachine> machines = problem.getMachines();
		List<IJob> jobs = problem.getJobs();

		double[] b = getB(problem);
		double[] a = getA(problem);
		double[] T = getT(problem);

		// TODO
		return 0;
	}

	private static double[] getB(IProblemInstance problem) {
		// TODO
		return null;
	}

	private static double[] getA(IProblemInstance problem) {
		// TODO
		return null;
	}

	private static double[] getT(IProblemInstance problem) {
		// TODO
		return null;
	}
}
