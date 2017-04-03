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
public class GP3POS extends GPNode{
    public String toString() { return "POS"; }

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

        children[0].eval(state,thread,input,stack,individual,problem);
        for (Job J:M.getQueue()) {
            if (J.tempPriority < 0) 
                J.tempPriority = 0;
        }
   }
}
