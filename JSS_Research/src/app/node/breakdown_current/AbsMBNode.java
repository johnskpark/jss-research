package app.node.breakdown_current;

import java.util.List;

import app.node.INode;
import jasima.shopSim.core.DowntimeSource;
import jasima.shopSim.core.IndividualMachine;
import jasima.shopSim.core.WorkStation;

public abstract class AbsMBNode implements INode {

	protected DowntimeSource getDowntimeSource(WorkStation machine) {
		IndividualMachine indMachine = machine.machDat()[0];

		List<DowntimeSource> srcs = indMachine.getDowntimeSources();
		if (srcs != null && !srcs.isEmpty()) {
			return srcs.get(0);
		} else {
			return null;
		}
	}

	// Bunch of useful getters.

	protected double getDeactivateTime(WorkStation machine) {
		DowntimeSource downSrc = getDowntimeSource(machine);
		if (downSrc == null) {
			// Returns that machine will breakdown at time infinity.
			return Double.POSITIVE_INFINITY;
		}

		return downSrc.getDeactivateTime();
	}

	protected double getActivateTime(WorkStation machine) {
		DowntimeSource downSrc = getDowntimeSource(machine);
		if (downSrc == null) {
			// Simply returns the current time.
			return machine.shop().simTime();
		}

		return downSrc.getActivateTime();
	}

	protected double getNextTimeBetweenFailures(WorkStation machine) {
		DowntimeSource downSrc = getDowntimeSource(machine);
		if (downSrc == null) {
			// Returns that machine will breakdown at time infinity.
			return Double.POSITIVE_INFINITY;
		}

		return downSrc.getNextTimeBetweenFailure();
	}

	protected double getNextRepairTime(WorkStation machine) {
		DowntimeSource downSrc = getDowntimeSource(machine);
		if (downSrc == null) {
			return 0.0;
		}

		return downSrc.getNextTimeToRepair();
	}

}
