/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.app.QCSP.func;
import ec.*;
import ec.app.QCSP.qcspData;
import ec.gp.*;
import ec.util.*;

/**
 *
 * @author nguyensu
 */
public class SCOREHoldingWorkload extends GPNode{
    public String toString() { return "HWL"; }

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

        qcspData jd = ((qcspData)(input));

        jd.tempVal = jd.HWL;
   }
}
