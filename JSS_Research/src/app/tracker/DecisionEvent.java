package app.tracker;

import jasima.core.util.Pair;
import jasima.shopSim.core.WorkStation;

public class DecisionEvent extends Pair<WorkStation, Double> {

	private static final long serialVersionUID = 7768965394498336195L;
	private static final double EPSILON = 0.001;

	public DecisionEvent(WorkStation machine, double simTime) {
		super(machine, simTime);
	}

	public WorkStation getMachine() {
		return a;
	}

	public int getMachineIndex() {
		return a.index();
	}

	public double getSimTime() {
		return b;
	}

	// TODO this doesn't get called wtf.
	@Override
	public boolean equals(Object o) {
		if (o == null || this.getClass() != o.getClass()) {
			return false;
		}

		DecisionEvent other = (DecisionEvent) o;
		if (this.getMachineIndex() != other.getMachineIndex()) {
			return false;
		} else if (Math.abs(this.getSimTime() - other.getSimTime()) > EPSILON) {
			return false;
		} else {
			return true;
		}
	}
}
