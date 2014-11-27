package jss.problem.dynamic_problem.rachel_dataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jss.problem.dynamic_problem.DynamicMachine;
import jss.problem.dynamic_problem.IProcessingOrderGenerator;

public class VariableOperationNumberPOG implements IProcessingOrderGenerator {

	private int minOperations;
	private int maxOperations;

	private long seed;
	private Random rand;

	public VariableOperationNumberPOG(int min, int max, long s) {
		minOperations = min;
		maxOperations = max;

		seed = s;
		rand = new Random(seed);
	}

	@Override
	public List<DynamicMachine> getProcessingOrder(List<DynamicMachine> machines) {
		List<DynamicMachine> selectableMachines = new ArrayList<DynamicMachine>(machines);
		List<DynamicMachine> operationOrder = new ArrayList<DynamicMachine>();

		int numOperations = rand.nextInt(maxOperations - minOperations) + minOperations;

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
