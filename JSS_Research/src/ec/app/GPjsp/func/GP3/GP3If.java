/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.app.GPjsp.func.GP3;
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
public class GP3If extends GPNode{
    public String toString() { return "If"; }

    public void checkConstraints(final EvolutionState state,
        final int tree,
        final GPIndividual typicalIndividual,
        final Parameter individualBase)
        {
        super.checkConstraints(state,tree,typicalIndividual,individualBase);
        if (children.length!=3)
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
        double[] cond_result = new double[M.getQueue().size()];
        double[] result = new double[M.getQueue().size()];

        children[0].eval(state,thread,input,stack,individual,problem);
        int j = 0;
        for (Job J:M.getQueue()) {
            cond_result[j] = J.tempPriority;
            j++;
        }
        j=0;
        children[1].eval(state,thread,input,stack,individual,problem);
        for (Job J:M.getQueue()) {
            result[j] = J.tempPriority;
            j++;
        }
        j=0;
        children[2].eval(state,thread,input,stack,individual,problem);
        for (Job J:M.getQueue()) {
            if (cond_result[j] > 0)
                J.tempPriority = result[j];
            j++;
        }        
   }
}
