/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.app.GPjsp.func;
import ec.*;
import ec.app.GPjsp.GPjsp;
import ec.app.GPjsp.jspData;
import ec.gp.*;
import ec.util.*;
import jsp.JSPFramework;
import jsp.Machine;
/**
 *
 * @author nguyensu
 */
public class CONDITIONLessEq extends GPNode{
 public String toString() { return "<="; }

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

        jspData jd = (jspData)(input);
        double attributeValue = -1;

        children[0].eval(state,thread,input,stack,individual,problem);

        children[1].eval(state,thread,input,stack,individual,problem);

        attributeValue = (int)(jd.attributeValue*9.99);

        if (attributeValue<=jd.attributeThreshold)
            jd.satisfyCONDITION = true;
        else jd.satisfyCONDITION = false;
   }
}
