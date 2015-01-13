package jss.problem.dynamic_problem.rachel_dataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import jss.IMachine;
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
	public List<IMachine> getProcessingOrder(Set<? extends IMachine> machines) {
		// TODO this is going to be slow. Also, I need to modify the event handlers to remove
		// jobs which have had events completed for them.
		List<IMachine> selectableMachines = new ArrayList<IMachine>(machines);
		List<IMachine> operationOrder = new ArrayList<IMachine>();

		int numOperations = ((maxOperations != minOperations) ? rand.nextInt(maxOperations - minOperations) : 0) + minOperations;

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
