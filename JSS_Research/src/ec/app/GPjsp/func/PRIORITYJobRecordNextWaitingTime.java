/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.app.GPjsp.func;
import ec.*;
import ec.app.GPjsp.jspData;
import ec.gp.*;
import ec.util.*;
import jsp.Job;
import jsp.Machine;
/**
 *
 * @author nguyensu
 */
public class PRIORITYJobRecordNextWaitingTime extends GPNode{
    public String toString() { return "RNWT"; }

    public void checkConstraints(final EvolutionState state,
        final int tree,
        final GPIndividual typicalIndividual,
        final Parameter individualBase)
        {
        super.checkConstraints(state,tree,typicalIndividual,individualBase);
        if (children.length!=0)
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

        jspData jd = ((jspData)(input));

        Machine M = jd.M;
        for (Job J:M.getQueue()) {
            int nextMachine = J.getNextMachine();
            if (nextMachine==-1) J.tempPriority = 0;
            else {
                J.tempPriority = J.getNextOperationWaitingTime();
                if (J.tempPriority == -1){
                    J.tempPriority = jd.abjsp.getMachines()[nextMachine].getQueueWorkload()/2.0;
                }
            }
        }
   }
}
