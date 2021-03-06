package ec.app.GPjsp.func.machineBreakdown;

import ec.EvolutionState;
import ec.Problem;
import ec.app.GPjsp.JSPData;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.util.Parameter;
import jsp.Job;
import jsp.Machine;

public class PriorityPredictiveBreakdownProb extends GPNode {

	private static final long serialVersionUID = 592970560560839962L;

	@Override
	public String toString() {
		return "PBP";
	}

	@Override
	public void checkConstraints(final EvolutionState state,
			final int tree,
			final GPIndividual typicalIndividual,
			final Parameter individualBase) {
        super.checkConstraints(state,tree,typicalIndividual,individualBase);
        if (children.length != 0) {
            state.output.error("Incorrect number of children for node " + toStringForError() + " at " + individualBase);
        }
	}

	@Override
	public void eval(final EvolutionState state,
			final int thread,
			final GPData input,
			final ADFStack stack,
			final GPIndividual individual,
			final Problem problem) {
        JSPData jd = (JSPData) input;

        // For now, assume exponential distribution for the breakdowns (memoryless).
        Machine machine = jd.machine;
        double predBreakdownRate = machine.getSampleAvgInterBreakdownTimes();

        for (Job job : machine.getQueue()) {
        	job.tempPriority = 1.0 - Math.exp(-job.getCurrentOperationProcessingTime() / predBreakdownRate);
        }
	}

}
