package jss.problem.dynamic_problem.rachel_dataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jss.problem.dynamic_problem.DynamicMachine;
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
	public List<DynamicMachine> getProcessingOrder(List<DynamicMachine> machines) {
		List<DynamicMachine> selectableMachines = new ArrayList<DynamicMachine>(machines);
		List<DynamicMachine> operationOrder = new ArrayList<DynamicMachine>();

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
