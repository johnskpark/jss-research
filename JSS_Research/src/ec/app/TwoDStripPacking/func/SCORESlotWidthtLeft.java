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
public class SCORESlotWidthtLeft extends GPNode{
    public String toString() { return "sWL"; }

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

        twoDStripData jd = ((twoDStripData)(input));

        jd.tempVal = jd.sWidthLeft;
   }
}
