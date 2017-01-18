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
/**
 *
 * @author nguyensu
 */
public class ERCattribute extends ERC{

    public int A_INDEX = -1;
    // making sure that we don't have any children is already
    // done in ERC.checkConstraints(), so we don't need to implement that.

    // this will produce numbers from [-1.0, 1.0), which is probably
    // okay but you might want to modify it if you don't like seeing
    // -1.0's occasionally showing up very rarely.

    public void resetNode(final EvolutionState state, final int thread)
        {
        if (A_INDEX == -1)
            A_INDEX = state.random[thread].nextInt(10);
        else {
            if (A_INDEX == 0)  A_INDEX++;
            else if (A_INDEX == 9) A_INDEX--;
            else if (state.random[thread].nextBoolean()) A_INDEX++;
            else A_INDEX--;
        }
    }

    public int nodeHashCode()
        {
        // a reasonable hash code
        return this.getClass().hashCode() + A_INDEX;
        }

    public boolean nodeEquals(final GPNode node)
        {
        // check first to see if we're the same kind of ERC --
        // won't work for subclasses; in that case you'll need
        // to change this to isAssignableTo(...)
        if (this.getClass() != node.getClass()) return false;
        // now check to see if the ERCs hold the same value
        return (((ERCattribute)node).A_INDEX == A_INDEX);
        }

    public void readNode(final EvolutionState state, final DataInput dataInput) throws IOException
        {
        A_INDEX = dataInput.readInt();
        }

    public void writeNode(final EvolutionState state, final DataOutput dataOutput) throws IOException
        {
        dataOutput.writeInt(A_INDEX);
        }

    public String encode()
        {
        return Code.encode(A_INDEX);
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
        A_INDEX = (int)dret.d;
        return true;
        }

    public String toStringForHumans()
        { return ""+A_INDEX; }

    public void eval(final EvolutionState state,
        final int thread,
        final GPData input,
        final ADFStack stack,
        final GPIndividual individual,
        final Problem problem)
        {
            jspData jd = ((jspData)(input));
            jd.attributeThreshold = A_INDEX;
        }
}
