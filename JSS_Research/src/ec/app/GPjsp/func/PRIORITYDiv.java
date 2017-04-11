/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.app.GPjsp.func;
import java.util.List;

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
/**
 *
 * @author nguyensu
 */
public class PRIORITYDiv extends GPNode {

	private static final double DIV_THRESHOLD = 0.00001;

	public String toString() {
		return "/";
	}

	public void checkConstraints(final EvolutionState state,
			final int tree,
			final GPIndividual typicalIndividual,
			final Parameter individualBase) {
		super.checkConstraints(state,tree,typicalIndividual,individualBase);
		if (children.length!=2) {
			state.output.error("Incorrect number of children for node " +
					toStringForError() + " at " +
					individualBase);
		}
	}

	public void eval(final EvolutionState state,
			final int thread,
			final GPData input,
			final ADFStack stack,
			final GPIndividual individual,
			final Problem problem) {

		JSPData jd = ((JSPData)(input));
		Machine machine = jd.machine;
		List<Job> queue = machine.getQueue();

		double[] result = new double[queue.size()];

		children[0].eval(state,thread,input,stack,individual,problem);
		for (int i = 0; i < queue.size(); i++) {
			result[i] = queue.get(i).tempPriority;
		}

		children[1].eval(state,thread,input,stack,individual,problem);
		for (int i = 0; i < queue.size(); i++) {
			Job job = queue.get(i);

			if (Math.abs(job.tempPriority) >= DIV_THRESHOLD) {
				job.tempPriority = result[i] / job.tempPriority;
			} else {
				job.tempPriority = 1;
			}
		}
	}
}
