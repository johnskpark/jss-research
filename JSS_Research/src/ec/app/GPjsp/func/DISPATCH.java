/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.app.GPjsp.func;
import ec.*;
import ec.app.GPjsp.JSPData;
import ec.gp.*;
import ec.util.*;
import jsp.AbstractJSPFramework;
import jsp.Machine;
/**
 *
 * @author bachsukyo412
 */
public class DISPATCH extends GPNode{
    public String toString() { return "DISPATCH"; }

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
        JSPData jd = (JSPData)(input);
        Machine M = jd.machine;
        AbstractJSPFramework jsp = jd.abJSP;
        children[0].eval(state,thread,input,stack,individual,problem);
        children[1].eval(state,thread,input,stack,individual,problem);
        jsp.calculatePriority(M);
   }
}
