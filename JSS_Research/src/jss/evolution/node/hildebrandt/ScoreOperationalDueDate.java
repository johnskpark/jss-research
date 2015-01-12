package jss.evolution.node.hildebrandt;

import jss.IJob;
import jss.IMachine;
import jss.evolution.JSSGPData;
import jss.node.NodeDefinition;
import jss.problem.dynamic_problem.DynamicJob;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.util.Parameter;

public class ScoreOperationalDueDate extends GPNode {

	private static final long serialVersionUID = 8501609114091690703L;

	@Override
	public String toString() {
		return NodeDefinition.SCORE_OPERATIONAL_DUE_DATE.toString();
	}

	@Override
	public void checkConstraints(final EvolutionState state,
			final int tree,
			final GPIndividual typicalIndividual,
			final Parameter individualBase) {
		super.checkConstraints(state, tree, typicalIndividual, individualBase);
		if (children.length != NodeDefinition.SCORE_OPERATIONAL_DUE_DATE.numChildren()) {
			state.output.error("Incorrect number of children for node " + toStringForError() + " at " + individualBase);
		}
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) {
		JSSGPData data = (JSSGPData)input;

		IMachine machine = data.getMachine();
		IJob job = data.getJob();

		double priority = getOperationalDueDate(machine, (DynamicJob)job);

		data.setPriority(priority);
	}

	private double getOperationalDueDate(IMachine machine, DynamicJob job) {
		if (job.getLastMachine() == null) {
			return job.getReadyTime() + job.getFlowFactor() * job.getProcessingTime(machine);
		}

		double previousODD = getOperationalDueDate(machine, job); // TODO
		return previousODD + job.getFlowFactor() * job.getProcessingTime(machine);
	}

}
