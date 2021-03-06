package ec.app.GPjsp.func.machineBreakdown;

import ec.EvolutionState;
import ec.Problem;
import ec.app.GPjsp.JSPData;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.util.Parameter;
import jsp.DynamicJSPFrameworkBreakdown;
import jsp.Job;
import jsp.Machine;

public class PriorityExactBreakdownProb extends GPNode {

	private static final long serialVersionUID = 8415406794300911723L;

	@Override
	public String toString() {
		return "EBP";
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
        DynamicJSPFrameworkBreakdown jsp = (DynamicJSPFrameworkBreakdown) jd.abJSP;
        double breakdownRate = jsp.getBreakdownRate();

        Machine machine = jd.machine;
        for (Job job : machine.getQueue()) {
        	job.tempPriority = 1.0 - Math.exp(-job.getCurrentOperationProcessingTime() / breakdownRate);
        }
	}

}
