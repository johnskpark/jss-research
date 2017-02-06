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

public class PriorityExactNextBreakdownTime extends GPNode {

	private static final long serialVersionUID = 7410067554225425587L;

	@Override
	public String toString() {
		return "ENBT";
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

        Machine machine = jd.M;
        double nextBreakdown = machine.getDeactivationTime();

        for (Job job : machine.getQueue()) {
        	job.tempPriority = nextBreakdown;
        }
	}

}
