/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.app.GPjsp.func;
import ec.*;
import ec.app.GPjsp.JSPData;
import ec.gp.*;
import ec.util.*;
import java.io.*;
import jsp.AbstractJSPFramework;
/**
 *
 * @author nguyensu
 */
public class ERCnondelayfactor extends ERC{
   public double NONDELAY_FACTOR = -1;
    // making sure that we don't have any children is already
    // done in ERC.checkConstraints(), so we don't need to implement that.

    // this will produce numbers from [-1.0, 1.0), which is probably
    // okay but you might want to modify it if you don't like seeing
    // -1.0's occasionally showing up very rarely.

    public void resetNode(final EvolutionState state, final int thread)
        {
        if (NONDELAY_FACTOR == -1)
            NONDELAY_FACTOR = state.random[thread].nextDouble();
        else {
            if (NONDELAY_FACTOR <= 0.05)  NONDELAY_FACTOR+=0.05;
            else if (NONDELAY_FACTOR >= 0.95) NONDELAY_FACTOR-=0.05;
            else if (state.random[thread].nextBoolean()) NONDELAY_FACTOR+=0.05;
            else NONDELAY_FACTOR-=0.05;
        }
    }

    public int nodeHashCode()
        {
        // a reasonable hash code
        return this.getClass().hashCode() + Float.floatToIntBits((float)NONDELAY_FACTOR);
        }

    public boolean nodeEquals(final GPNode node)
        {
        // check first to see if we're the same kind of ERC --
        // won't work for subclasses; in that case you'll need
        // to change this to isAssignableTo(...)
        if (this.getClass() != node.getClass()) return false;
        // now check to see if the ERCs hold the same value
        return (((ERCnondelayfactor)node).NONDELAY_FACTOR == NONDELAY_FACTOR);
        }

    public void readNode(final EvolutionState state, final DataInput dataInput) throws IOException
        {
        NONDELAY_FACTOR = dataInput.readDouble();
        }

    public void writeNode(final EvolutionState state, final DataOutput dataOutput) throws IOException
        {
        dataOutput.writeDouble(NONDELAY_FACTOR);
        }

    public String encode()
        {
        return Code.encode(NONDELAY_FACTOR);
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
        NONDELAY_FACTOR = (int)dret.d;
        return true;
        }

    public String toStringForHumans()
        { return ""+(float)NONDELAY_FACTOR; }

    public void eval(final EvolutionState state,
        final int thread,
        final GPData input,
        final ADFStack stack,
        final GPIndividual individual,
        final Problem problem)
        {
            JSPData jd = (JSPData)(input);
            AbstractJSPFramework jsp = jd.abjsp;
            
            jsp.setNonDelayFactor(NONDELAY_FACTOR);
        }
}
