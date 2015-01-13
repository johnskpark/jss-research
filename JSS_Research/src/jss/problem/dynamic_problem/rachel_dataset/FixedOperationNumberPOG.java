package jss.problem.dynamic_problem.rachel_dataset;

import java.util.ArrayList;
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

	private int numOperations;

	private long seed;
	private Random rand;

	/**
	 * TODO javadoc.
	 * @param num
	 * @param s
	 */
	public FixedOperationNumberPOG(int num, long s) {
		numOperations = num;

		seed = s;
		rand = new Random(seed);
	}

	@Override
	public List<IMachine> getProcessingOrder(Set<IMachine> machines) {
		List<IMachine> selectableMachines = new ArrayList<IMachine>(machines);
		List<IMachine> operationOrder = new ArrayList<IMachine>();

		int operation = 0;
		while (operation < numOperations && !selectableMachines.isEmpty()) {
			int index = rand.nextInt(selectableMachines.size());
			operationOrder.add(selectableMachines.remove(index));
			operation++;
		}

		return operationOrder;
	}

	@Override
	public void reset() {
		rand = new Random(seed);
	}

}
