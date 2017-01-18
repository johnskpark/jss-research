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
public class PRIORITYJobSmallestExpectedTardiness extends GPNode{
    public String toString() { return "SET"; }

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
            double earliestStartTime = (M.getReadyTime()>J.getReadyTime())? M.getReadyTime():J.getReadyTime();
            if (earliestStartTime + J.getRemainingProcessingTime()-J.getDuedate()>0)
                J.tempPriority = earliestStartTime + J.getRemainingProcessingTime()-J.getDuedate();
            else J.tempPriority = 0;
        }
   }
}
