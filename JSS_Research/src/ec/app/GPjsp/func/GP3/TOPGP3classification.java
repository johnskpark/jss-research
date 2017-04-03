/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.app.GPjsp.func.GP3;
import ec.*;
import ec.app.GPjsp.JSPData;
import ec.gp.*;
import ec.util.*;
import jsp.Machine;
/**
 *
 * @author nguyensu
 */
public class TOPGP3classification extends GPNode{
    public String toString() { return "Classification"; }

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
        
        Machine thisM  = jd.machine;
        int thisID = jd.machine.getID();
        
        double[] attribute = new double[jd.abJSP.getMachines().length];
        
        
        for (int i = 0; i < attribute.length; i++) {
            jd.machine = jd.abJSP.getMachines()[i];
            children[0].eval(state,thread,input,stack,individual,problem);
            attribute[i] = jd.tempVal;
        }  
        
        jd.machine = thisM;
        
        boolean cond = true;
        for (int i = 0; i < attribute.length; i++) {
            if (attribute[i]>attribute[thisID]) {
                cond = false;
                break;
            }
        }
        
       
        if (cond){
            children[1].eval(state,thread,input,stack,individual,problem);
        } else{
            children[2].eval(state,thread,input,stack,individual,problem);
        }      
   }
}
