package ec.app.GPjsp.func.DDA.machineBreakdown;

import ec.EvolutionState;
import ec.Problem;
import ec.app.GPjsp.JSPData;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.util.Parameter;

public class ESTPredictiveNextBreakdownTime extends GPNode {

	private static final long serialVersionUID = 6314694437300222802L;

	@Override
	public String toString() {
		return "PNBT";
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
        jd.tempVal = jd.stat.prevATime + jd.stat.sampleInterBreakdownTimes;
	}

}
