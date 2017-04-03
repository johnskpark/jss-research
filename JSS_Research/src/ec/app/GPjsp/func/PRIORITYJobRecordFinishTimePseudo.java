/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.app.GPjsp.func;
import ec.*;
import ec.app.GPjsp.JSPData;
import ec.gp.*;
import ec.util.*;
import jsp.Job;
import jsp.Machine;
/**
 *
 * @author nguyensu
 */
public class PRIORITYJobRecordFinishTimePseudo extends GPNode{
    public String toString() { return "rft"; }

    public void checkConstraints(final EvolutionState state,
        final int tree,
        final GPIndividual typicalIndividual,
        final Parameter individualBase)
        {
        super.checkConstraints(state,tree,typicalIndividual,individualBase);
        if (children.length!=1)
            state.output.error("Incorrect number of children for node " +
                toStringForError() + " at " +
                individualBase);
        }

    public void eval(final EvolutionState state,
        final int thread,
        final GPData input,
        final ADFStack stack,
        final GPIndividual individual,
        final Problem problem) {

        JSPData jd = ((JSPData)(input));

        Machine M = jd.machine;
        
        for (Job J:M.getQueue()) {
            if (jd.firstIteration) {
                children[0].eval(state,thread,input,stack,individual,problem);
            }
            else J.tempPriority = J.getFinishTime();
        }
   }
}
