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
import jsp.Job;
import jsp.Machine;
/**
 *
 * @author nguyensu
 */
public class ERCrandom extends ERC{

	public double r_constant = -1;

    // making sure that we don't have any children is already
    // done in ERC.checkConstraints(), so we don't need to implement that.

    // this will produce numbers from [-1.0, 1.0), which is probably
    // okay but you might want to modify it if you don't like seeing
    // -1.0's occasionally showing up very rarely.

    public void resetNode(final EvolutionState state, final int thread) {
        if (r_constant == -1) {
            r_constant = state.random[thread].nextDouble();
        } else {
            if (r_constant <= 0.05) { r_constant += 0.05; }
            else if (r_constant >= 0.95) { r_constant -= 0.05; }
            else if (state.random[thread].nextBoolean()) { r_constant += 0.05; }
            else { r_constant -= 0.05; }
        }
    }

    public int nodeHashCode() {
    	// a reasonable hash code
    	return this.getClass().hashCode() + Float.floatToIntBits((float) r_constant);
    }

    public boolean nodeEquals(final GPNode node) {
    	// check first to see if we're the same kind of ERC --
    	// won't work for subclasses; in that case you'll need
    	// to change this to isAssignableTo(...)
    	if (this.getClass() != node.getClass()) {
    		return false;
    	}

    	// now check to see if the ERCs hold the same value
    	return (((ERCrandom)node).r_constant == r_constant);
    }

    public void readNode(final EvolutionState state, final DataInput dataInput) throws IOException {
    	r_constant = dataInput.readDouble();
    }

    public GPNode readNode(final DecodeReturn dret) {
    	int len = dret.data.length();
    	int originalPos = dret.pos;

    	ERCrandom node = (ERCrandom) lightClone();

    	// Iterate through the data to find potential match
    	while (dret.pos < len &&
    			!Character.isWhitespace(dret.data.charAt(dret.pos)) &&
    			dret.data.charAt(dret.pos) != ')' &&
    			dret.data.charAt(dret.pos) != '(') {
    		dret.pos++;
    	}
    	String potentialMatch = dret.data.substring(originalPos, dret.pos);

    	// Attempt to convert the potential match to a double.
    	try {
    		double constant = Double.parseDouble(potentialMatch);
    		node.r_constant = constant;
    		return node;
    	} catch (NumberFormatException ex) {
    		// Couldn't convert to a double, revert the position back to the original location.
    		dret.pos = originalPos;
    		return null;
    	}
    }

    public void writeNode(final EvolutionState state, final DataOutput dataOutput) throws IOException {
        dataOutput.writeDouble(r_constant);
        }

    public String encode() {
        return Code.encode(r_constant);
    }

    public boolean decode(DecodeReturn dret) {
    	// store the position and the string in case they
    	// get modified by Code.java
    	int pos = dret.pos;
    	String data = dret.data;

    	// decode
    	Code.decode(dret);

    	if (dret.type != DecodeReturn.T_DOUBLE)  {
    		// restore the position and the string; it was an error
    		dret.data = data;
    		dret.pos = pos;
    		return false;
    	}

    	// store the data
    	r_constant = (int)dret.d;
    	return true;
    }

    public String toStringForHumans() {
    	return "" + (float) r_constant;
    }

    public void eval(final EvolutionState state,
    		final int thread,
    		final GPData input,
    		final ADFStack stack,
    		final GPIndividual individual,
    		final Problem problem) {
    	JSPData jd = ((JSPData)(input));

    	Machine M = jd.M;

    	for (Job J:M.getQueue()) {
    		J.tempPriority = r_constant;
    	}
    }
}
