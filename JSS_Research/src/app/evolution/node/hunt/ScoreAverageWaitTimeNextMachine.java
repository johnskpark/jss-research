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

// The average wait time of last five jobs processed at the next machine job visits.
public class ScoreAverageWaitTimeNextMachine extends GPNode {

	private static final long serialVersionUID = -8680402164419018880L;
	private static final int NUM_JOBS_SAMPLED = 5;

	@Override
	public String toString() {
		return NodeDefinition.SCORE_AVERAGE_WAIT_TIME_NEXT_MACHINE.toString();
	}

	@Override
	public void checkConstraints(final EvolutionState state,
			final int tree,
			final GPIndividual typicalIndividual,
			final Parameter individualBase) {
		super.checkConstraints(state, tree, typicalIndividual, individualBase);
		if (children.length != NodeDefinition.SCORE_AVERAGE_WAIT_TIME_NEXT_MACHINE.numChildren()) {
			state.output.error("Incorrect number of children for node " + toStringForError() + " at " + individualBase);
		}
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) {
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
//			for (int i = 0; i < Math.min(completedJobsQueue.size(), NUM_JOBS_SAMPLED); i++) {
//				PrioRuleTarget completedJobsQueue.
//
//				averageWaitTime += 0.0; // TODO
//			}
			averageWaitTime /= completedJobsQueue.size();

			data.setPriority(averageWaitTime);
		}
	}

}
