package app.evolution.node.breakdown;

import app.evolution.JasimaGPData;
import app.evolution.node.SingleLineGPNode;
import app.listener.breakdown.BreakdownListener;
import app.node.NodeDefinition;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import jasima.core.statistics.SummaryStat;

public class ScoreAverageRepairTimeAllMachines extends SingleLineGPNode {

	private static final long serialVersionUID = -2088494524904208735L;
	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_AVERAGE_REPAIR_TIME_ALL_MACHINES;

	@Override
	public String toString() {
		return NODE_DEFINITION.toString();
	}

	@Override
	public int expectedChildren() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public void eval(final EvolutionState state,
			final int thread,
			final GPData input,
			final ADFStack stack,
			final GPIndividual individual,
			final Problem problem) {
		JasimaGPData data = (JasimaGPData) input;

		BreakdownListener listener = (BreakdownListener) data.getWorkStationListener(BreakdownListener.class.getSimpleName());

		if (listener.hasBeenRepairedAnyMachine()) {
			SummaryStat repairStat = listener.getAllMachineRepairStat();

			data.setPriority(repairStat.mean());
		} else {
			data.setPriority(0.0);
		}
	}

}
