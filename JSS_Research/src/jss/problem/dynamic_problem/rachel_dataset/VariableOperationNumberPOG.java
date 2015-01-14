package jss.problem.dynamic_problem.rachel_dataset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import jss.IMachine;
import jss.problem.dynamic_problem.IProcessingOrderGenerator;

public class VariableOperationNumberPOG implements IProcessingOrderGenerator {

	private List<? extends IMachine> machines;
	private int minOperations;
	private int maxOperations;

	private long seed;
	private Random rand;

	public VariableOperationNumberPOG(Set<? extends IMachine> machines, int min, int max, long s) {
		this.machines = new ArrayList<IMachine>(machines);
		this.minOperations = min;
		this.maxOperations = max;

		seed = s;
		rand = new Random(seed);
	}

	@Override
	public List<IMachine> getProcessingOrder() {
		List<IMachine> operationOrder = new ArrayList<IMachine>();

		Collections.shuffle(machines, rand);

		int numOperations = ((maxOperations != minOperations) ? rand.nextInt(maxOperations - minOperations) : 0) + minOperations;

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
