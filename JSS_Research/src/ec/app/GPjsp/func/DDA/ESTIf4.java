/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.app.GPjsp.func.DDA;
import ec.*;
import ec.app.GPjsp.jspData;
import ec.gp.*;
import ec.util.*;
/**
 *
 * @author nguyensu
 */
public class ESTIf4 extends GPNode{
    public String toString() { return "If"; }

    public void checkConstraints(final EvolutionState state,
        final int tree,
        final GPIndividual typicalIndividual,
        final Parameter individualBase)
        {
        super.checkConstraints(state,tree,typicalIndividual,individualBase);
        if (children.length!=4)
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

        children[0].eval(state,thread,input,stack,individual,problem);
        double cond_result1 = jd.tempVal;
        children[1].eval(state,thread,input,stack,individual,problem);
        double cond_result2 = jd.tempVal;
        if (cond_result1 >= cond_result2){
            children[2].eval(state,thread,input,stack,individual,problem);
        } else{
            children[3].eval(state,thread,input,stack,individual,problem);
        }      
   }
}
