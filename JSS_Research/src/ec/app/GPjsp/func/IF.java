/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.app.GPjsp.func;
import ec.*;
import ec.app.GPjsp.jspData;
import ec.gp.*;
import ec.util.*;
/**
 *
 * @author bachsukyo412
 */
public class IF extends GPNode{
    public String toString() { return "IF"; }

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

        jspData rd = ((jspData)(input));

        children[0].eval(state,thread,input,stack,individual,problem);
        if (rd.satisfyCONDITION) children[1].eval(state,thread,input,stack,individual,problem);
        else children[2].eval(state,thread,input,stack,individual,problem);

   }
}
