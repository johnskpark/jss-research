package jss.evolution.node.hildebrandt;

import jss.IJob;
import jss.IMachine;
import jss.evolution.JSSGPData;
import jss.node.NodeDefinition;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.util.Parameter;

public class ScoreWorkInNextQueue extends GPNode {

	private static final long serialVersionUID = 8357230245031223412L;

	@Override
	public String toString() {
		return NodeDefinition.SCORE_WORK_IN_NEXT_QUEUE.toString();
	}

	@Override
	public void checkConstraints(final EvolutionState state,
			final int tree,
			final GPIndividual typicalIndividual,
			final Parameter individualBase) {
		super.checkConstraints(state, tree, typicalIndividual, individualBase);
		if (children.length != NodeDefinition.SCORE_WORK_IN_NEXT_QUEUE.numChildren()) {
			state.output.error("Incorrect number of children for node " + toStringForError() + " at " + individualBase);
		}
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) {
		JSSGPData data = (JSSGPData)input;

		IJob job = data.getJob();
		IMachine machine = job.getNextMachine();

		double priority = 0;
		if (machine != null) {
			for (IJob waitingJob : machine.getWaitingJobs()) {
				priority += waitingJob.getProcessingTime(machine);
			}

			IJob currentJob;
			if ((currentJob = machine.getCurrentJob()) != null) {
				priority += currentJob.getProcessingTime(machine);
			}
		}

		data.setPriority(priority);
	}

}
