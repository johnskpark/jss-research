/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.app.TwoDStripPacking.func;
import ec.*;
import ec.app.TwoDStripPacking.twoDStripData;
import ec.gp.*;
import ec.util.*;

/**
 *
 * @author nguyensu
 */
public class SCOREIf extends GPNode{
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

        twoDStripData jd = ((twoDStripData)(input));

        children[0].eval(state,thread,input,stack,individual,problem);
        double cond_result = jd.tempVal;

        children[1].eval(state,thread,input,stack,individual,problem);
        double result = jd.tempVal;

        children[2].eval(state,thread,input,stack,individual,problem);
        if (cond_result >= 0)
            jd.tempVal = result;

   }
}
