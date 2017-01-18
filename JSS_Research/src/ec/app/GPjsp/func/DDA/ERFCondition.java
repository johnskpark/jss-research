/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.app.GPjsp.func.DDA;
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
public class ERFCondition extends ERF{
    public static double[] upper = {0.25,0.50,0.75,Double.POSITIVE_INFINITY};
    public static double[] lower = {0.0,0.25,0.50,0.75};
    public static String[] attributeName = {"SU","SLR","LR","PO"};
    public int range = -1;
    public int attribute = -1;
    // making sure that we don't have any children is already
    // done in ERC.checkConstraints(), so we don't need to implement that.

    // this will produce numbers from [-1.0, 1.0), which is probably
    // okay but you might want to modify it if you don't like seeing
    // -1.0's occasionally showing up very rarely.

    public void resetNode(final EvolutionState state, final int thread)
    {
        range = state.random[thread].nextInt(lower.length);
        attribute = state.random[thread].nextInt(attributeName.length);
    }

    public int nodeHashCode()
        {
        // a reasonable hash code
        return this.getClass().hashCode() + attribute + Float.floatToIntBits((float)range);
        }

    public boolean nodeEquals(final GPNode node)
        {
        // check first to see if we're the same kind of ERC --
        // won't work for subclasses; in that case you'll need
        // to change this to isAssignableTo(...)
        if (this.getClass() != node.getClass()) return false;
        // now check to see if the ERCs hold the same value
        return ((((ERFCondition)node).range == range)
                &&(((ERFCondition)node).attribute == attribute));
        }

    public void readNode(final EvolutionState state, final DataInput dataInput) throws IOException
        {
        range = dataInput.readInt();
        attribute = dataInput.readInt();
        }

    public void writeNode(final EvolutionState state, final DataOutput dataOutput) throws IOException
        {
        dataOutput.writeDouble(range);
        dataOutput.writeDouble(attribute);
        }

    public String encode()
        {
        return Code.encode(attribute*1000000 + range);
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
        range = (int) encodedValue;
        return true;
        }

    public String toStringForHumans()
        { return "S_"+attributeName[attribute]+"["+range +"]"; }

    public void eval(final EvolutionState state,
        final int thread,
        final GPData input,
        final ADFStack stack,
        final GPIndividual individual,
        final Problem problem)
        {
            JSPData jd = ((JSPData)(input));
            
            double attributeValue = -1;
            if (attribute == 0) attributeValue = jd.stat.SAR*jd.stat.SAPR;
            else if (attribute == 1) attributeValue = jd.stat.SL/jd.stat.M;
            else if (attribute == 2) attributeValue = jd.stat.NO/jd.stat.M;
            else if (attribute == 3) attributeValue = jd.stat.kTH/jd.stat.NO;
            else if (attribute == 4) attributeValue = jd.stat.PEF/(1.0/(1.0/jd.stat.TSAPR-jd.stat.SAR));
            
            if (isInRange(attributeValue, range)){
                children[0].eval(state,thread,input,stack,individual,problem);
            } else{
                children[1].eval(state,thread,input,stack,individual,problem);
            } 
            
        }
    public boolean isInRange(double a, int r){
        return (a>=lower[r]&&a<=upper[r]);
    }
}
