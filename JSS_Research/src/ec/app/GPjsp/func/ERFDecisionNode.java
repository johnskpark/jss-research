/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.app.GPjsp.func;
import ec.*;
import ec.app.GPjsp.Operators.ERF;
import ec.app.GPjsp.JSPData;
import ec.gp.*;
import ec.util.*;
import java.io.*;
import jsp.AbstractJSPFramework;
import jsp.Job;
import jsp.Machine;
/**
 *
 * @author nguyensu
 */
public class ERFDecisionNode extends ERF{
    public static String[] attName = {"BWR","CMI","CWR","DJ","MP","WR"};
    public double threshold = -1;
    public int attribute = -1;
    // making sure that we don't have any children is already
    // done in ERC.checkConstraints(), so we don't need to implement that.

    // this will produce numbers from [-1.0, 1.0), which is probably
    // okay but you might want to modify it if you don't like seeing
    // -1.0's occasionally showing up very rarely.

    public void resetNode(final EvolutionState state, final int thread)
    {
        threshold = state.random[thread].nextDouble();
        attribute = state.random[thread].nextInt(6);
    }

    public int nodeHashCode()
        {
        // a reasonable hash code
        return this.getClass().hashCode() + attribute + Float.floatToIntBits((float)threshold);
        }

    public boolean nodeEquals(final GPNode node)
        {
        // check first to see if we're the same kind of ERC --
        // won't work for subclasses; in that case you'll need
        // to change this to isAssignableTo(...)
        if (this.getClass() != node.getClass()) return false;
        // now check to see if the ERCs hold the same value
        return ((((ERFDecisionNode)node).threshold == threshold)&&(((ERFDecisionNode)node).attribute == attribute));
        }

    public void readNode(final EvolutionState state, final DataInput dataInput) throws IOException
        {
        threshold = dataInput.readDouble();
        attribute = dataInput.readInt();
        }

    public void writeNode(final EvolutionState state, final DataOutput dataOutput) throws IOException
        {
        dataOutput.writeDouble(threshold);
        dataOutput.writeDouble(attribute);
        }

    public String encode()
        {
        return Code.encode(attribute*1000000 + threshold);
    }

    public boolean decode(DecodeReturn dret)
        {
        // store the position and the string in case they
        // get modified by Code.java
        int pos = dret.pos;
        String data = dret.data;

        // decode
        Code.decode(dret);

        if (dret.type != DecodeReturn.T_DOUBLE) // uh oh!
            {
            // restore the position and the string; it was an error
            dret.data = data;
            dret.pos = pos;
            return false;
            }

        // store the data
        double encodedValue = dret.d;
        attribute = (int)encodedValue/1000000;
        encodedValue -= attribute*1000000;
        return true;
        }

    public String toStringForHumans()
        { return "Check("+attName[attribute] + ">=" + (float)threshold + ")"; }

    public void eval(final EvolutionState state,
        final int thread,
        final GPData input,
        final ADFStack stack,
        final GPIndividual individual,
        final Problem problem)
        {
            JSPData jd = ((JSPData)(input));
            Machine M = jd.machine;
            AbstractJSPFramework jsp = jd.abJSP;
            
            double attributeValue = -1;
            if (attribute == 0) attributeValue = M.getBottleNeckRatioOfQueue(jsp.getBottleneckMachineID());
            else if (attribute == 1) attributeValue = jsp.getCriticalMachineIdleness();
            else if (attribute == 2) attributeValue = M.getCritialRatioOfQueue(jsp.getCriticalMachineID());
            else if (attribute == 3) attributeValue = M.getDeviationInQueue();
            else if (attribute == 4) attributeValue = M.getMachineProgress();
            else if (attribute == 5) attributeValue = M.getWorkLoadRatio();

            //double weight = 1/(1+Math.exp(shape*shift-shape*(-1+2*attributeValue)));
            
            if (attributeValue>=threshold){
                children[0].eval(state,thread,input,stack,individual,problem);
            } else {
                children[1].eval(state,thread,input,stack,individual,problem);
            }
        }
}
