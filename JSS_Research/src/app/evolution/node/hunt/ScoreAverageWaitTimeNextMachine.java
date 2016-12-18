package app.evolution.node.hunt;

import java.util.Map;
import java.util.Queue;

import app.JasimaWorkStationListener;
import app.evolution.JasimaGPData;
import app.evolution.node.SingleLineGPNode;
import app.listener.hunt.HuntListener;
import app.listener.hunt.OperationCompletionStat;
import app.node.NodeDefinition;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;

// The average wait time of last five jobs processed at the next machine job visits.
public class ScoreAverageWaitTimeNextMachine extends SingleLineGPNode {

	private static final long serialVersionUID = -8680402164419018880L;
	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_AVERAGE_WAIT_TIME_NEXT_MACHINE;

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

		Map<String, JasimaWorkStationListener> listeners = data.getWorkStationListeners();
		HuntListener listener = (HuntListener) listeners.get(HuntListener.class.getSimpleName());

		int nextTask = entry.getTaskNumber() + 1;
		if (nextTask >= entry.numOps()) {
			data.setPriority(0.0);
		} else {
			WorkStation machine = entry.getOps()[nextTask].machine;

			if (listener.hasCompletedJobs(machine)) {
				Queue<OperationCompletionStat> completedJobsQueue = listener.getLastCompletedJobs(machine);

				double averageWaitTime = 0.0;
				for (OperationCompletionStat stat : completedJobsQueue) {
					averageWaitTime += stat.getWaitTime();
				}
				averageWaitTime /= completedJobsQueue.size();

				data.setPriority(averageWaitTime);
			} else {
				data.setPriority(0.0);
			}
		}
	}

}
