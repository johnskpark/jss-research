package app.evolution.node.hunt;

import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;

import java.util.Queue;

import app.evolution.JasimaGPData;
import app.listener.hunt.HuntListener;
import app.listener.hunt.OperationCompletionStats;
import app.node.NodeDefinition;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.util.Parameter;

//The average wait time of last five jobs processed all machines on the shop floor.
public class ScoreAverageWaitTimeAllMachine extends GPNode {

	private static final long serialVersionUID = -2890903764607495129L;
	private static final int NUM_JOBS_SAMPLED = 5;

	@Override
	public String toString() {
		return NodeDefinition.SCORE_AVERAGE_WAIT_TIME_ALL_MACHINE.toString();
	}

	@Override
	public void checkConstraints(final EvolutionState state,
			final int tree,
			final GPIndividual typicalIndividual,
			final Parameter individualBase) {
		super.checkConstraints(state, tree, typicalIndividual, individualBase);
		if (children.length != NodeDefinition.SCORE_AVERAGE_WAIT_TIME_ALL_MACHINE.numChildren()) {
			state.output.error("Incorrect number of children for node " + toStringForError() + " at " + individualBase);
		}
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) {
		// TODO for all machines.
		JasimaGPData data = (JasimaGPData) input;
		PrioRuleTarget entry = data.getPrioRuleTarget();
		HuntListener listener = (HuntListener) data.getWorkStationListener();

		int nextTask = entry.getTaskNumber() + 1;
		if (nextTask >= entry.numOps()) {
			data.setPriority(0);
		} else {
			WorkStation machine = entry.getOps()[nextTask].machine;

			Queue<OperationCompletionStats> completedJobsQueue = listener.getLastCompletedJobs(machine);

			double averageWaitTime = 0.0;
			for (OperationCompletionStats stat : completedJobsQueue) {
//				averageWaitTime += nextEntry.get;
			}
			averageWaitTime /= completedJobsQueue.size();

			data.setPriority(averageWaitTime);
		}
	}

}
