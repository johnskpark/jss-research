/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.app.GPjsp.func.Machine;
import ec.app.GPjsp.func.*;
import ec.*;
import ec.app.GPjsp.JSPData;
import ec.gp.*;
import ec.util.*;

/**
 *
 * @author nguyensu
 */
public class MACHINEMin extends GPNode{
    public String toString() { return "Min"; }

    public void checkConstraints(final EvolutionState state,
        final int tree,
        final GPIndividual typicalIndividual,
        final Parameter individualBase)
        {
        super.checkConstraints(state,tree,typicalIndividual,individualBase);
        if (children.length!=2)
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

        children[0].eval(state,thread,input,stack,individual,problem);

        double result = jd.tempVal;

        children[1].eval(state,thread,input,stack,individual,problem);
        
        if (jd.tempVal>result) jd.tempVal = result;
   }
}
