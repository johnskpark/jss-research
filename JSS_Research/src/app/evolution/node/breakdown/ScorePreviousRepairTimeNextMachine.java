package app.evolution.node.breakdown;

import java.util.Map;

import app.IWorkStationListener;
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
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;

public class ScorePreviousRepairTimeNextMachine extends SingleLineGPNode {

	private static final long serialVersionUID = 6813309384142207441L;
	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_PREVIOUS_REPAIR_TIME_NEXT_MACHINE;

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
		PrioRuleTarget entry = data.getPrioRuleTarget();

		Map<String, IWorkStationListener> listeners = data.getWorkStationListeners();
		BreakdownListener listener = (BreakdownListener) listeners.get(BreakdownListener.class.getSimpleName());

		int nextTask = entry.getTaskNumber() + 1;
		if (nextTask >= entry.numOps()) {
			data.setPriority(0.0);
		} else {
			WorkStation machine = entry.getOps()[nextTask].machine;

			if (listener.hasBeenRepaired(machine)) {
				SummaryStat repairStat = listener.getMachineRepairTimeStat(machine);

				data.setPriority(repairStat.lastValue());
			} else {
				data.setPriority(0.0);
			}
		}
	}

}
