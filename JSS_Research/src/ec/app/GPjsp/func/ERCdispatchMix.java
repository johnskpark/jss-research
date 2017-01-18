/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.app.GPjsp.func;
import ec.*;
import ec.app.GPjsp.jspData;
import ec.gp.*;
import ec.util.*;
import java.io.*;
import jsp.AbstractJSPFramework;
import jsp.Machine;
/**
 *
 * @author nguyensu
 */
public class ERCdispatchMix extends ERC{
    public double nonDelayFactor;
    public Machine.priorityType PT;
    // making sure that we don't have any children is already
    // done in ERC.checkConstraints(), so we don't need to implement that.

    // this will produce numbers from [-1.0, 1.0), which is probably
    // okay but you might want to modify it if you don't like seeing
    // -1.0's occasionally showing up very rarely.
    private void setPriorityTypefromIndex(int priorityIndex){
        if (priorityIndex == 0) PT = Machine.priorityType.FCFS;
        else if (priorityIndex == 1) PT = Machine.priorityType.SPT;
        else if (priorityIndex == 2) PT = Machine.priorityType.LPT;
        else if (priorityIndex == 3) PT = Machine.priorityType.LSO;
        else if (priorityIndex == 4) PT = Machine.priorityType.LRM;
        else if (priorityIndex == 5) PT = Machine.priorityType.MWKR;
        else if (priorityIndex == 6) PT = Machine.priorityType.SWKR;
        else if (priorityIndex == 7) PT = Machine.priorityType.MOPR;
        else if (priorityIndex == 8) PT = Machine.priorityType.EDD;
        else if (priorityIndex == 9) PT = Machine.priorityType.MS;
        else if (priorityIndex == 10) PT = Machine.priorityType.WSPT;
    }
    public void resetNode(final EvolutionState state, final int thread)
        {
        nonDelayFactor = state.random[thread].nextDouble();
        int priorityIndex = state.random[thread].nextInt(11);
        setPriorityTypefromIndex(priorityIndex);
    }

    public int nodeHashCode()
        {
        // a reasonable hash code
        return this.getClass().hashCode() + Float.floatToIntBits((float)nonDelayFactor + PT.ordinal());
        }

    public boolean nodeEquals(final GPNode node)
        {
        // check first to see if we're the same kind of ERC --
        // won't work for subclasses; in that case you'll need
        // to change this to isAssignableTo(...)
        if (this.getClass() != node.getClass()) return false;
        // now check to see if the ERCs hold the same value
        return (((ERCdispatchMix)node).nonDelayFactor == nonDelayFactor &&
                ((ERCdispatchMix)node).PT == PT);
        }

    public void readNode(final EvolutionState state, final DataInput dataInput) throws IOException
        {
        nonDelayFactor = dataInput.readDouble();
        int priorityIndex = dataInput.readInt();
        setPriorityTypefromIndex(priorityIndex);
        }

    public void writeNode(final EvolutionState state, final DataOutput dataOutput) throws IOException
        {
        dataOutput.writeDouble(nonDelayFactor);
        dataOutput.writeInt(PT.ordinal());
        }

    public String encode()
        { 
        return Code.encode(nonDelayFactor+PT.ordinal());
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
        int priorityIndex = (int) encodedValue;
        setPriorityTypefromIndex(priorityIndex);
        nonDelayFactor = encodedValue - priorityIndex;
        return true;
        }

    public String toStringForHumans()
        { return "(DISPATCH " + (float)nonDelayFactor + " " + PT.name() +")"; }

    public void eval(final EvolutionState state,
        final int thread,
        final GPData input,
        final ADFStack stack,
        final GPIndividual individual,
        final Problem problem)
        {
            jspData jd = (jspData)(input);
            Machine M = jd.M;
            
            AbstractJSPFramework jsp = jd.abjsp;
            
            jsp.setNonDelayFactor(nonDelayFactor);
            
            jsp.setPriorityType(PT);

            jsp.calculatePriority(M);
        }
}
