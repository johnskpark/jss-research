package jss.problem.dynamic_problem.rachel_dataset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import jss.IMachine;
import jss.problem.dynamic_problem.IProcessingOrderGenerator;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class FixedOperationNumberPOG implements IProcessingOrderGenerator {

	private List<? extends IMachine> machines;
	private int numOperations;

	private long seed;
	private Random rand;

	/**
	 * TODO javadoc.
	 * @param num
	 * @param s
	 */
	public FixedOperationNumberPOG(Set<? extends IMachine> machines, int num, long s) {
		this.machines = new ArrayList<IMachine>(machines);
		this.numOperations = num;

		seed = s;
		rand = new Random(seed);
	}

	@Override
	public List<IMachine> getProcessingOrder() {
		List<IMachine> operationOrder = new ArrayList<IMachine>();

		Collections.shuffle(machines, rand);

		for (int i = 0; i < numOperations; i++) {
			operationOrder.add(machines.get(i));
		}

		return operationOrder;
	}

	@Override
	public void reset() {
		rand = new Random(seed);
	}

}
