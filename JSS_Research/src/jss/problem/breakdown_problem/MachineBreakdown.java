package jss.problem.breakdown_problem;

import java.util.ArrayList;
import java.util.List;

import jss.IEventHandler;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public abstract class MachineBreakdown implements IEventHandler {

	private List<BreakdownMachine> machines = new ArrayList<BreakdownMachine>();

	public MachineBreakdown() {

	}

	public void addMachine(BreakdownMachine machine) {
		machines.add(machine);
	}

	protected List<BreakdownMachine> getMachines() {
		return machines;
	}

}
