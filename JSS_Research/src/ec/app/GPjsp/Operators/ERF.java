/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.app.GPjsp.Operators;
import ec.*;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.util.*;
import java.io.*;
/**
 *
 * @author nguyensu
 */
public abstract class ERF extends GPNode
    {
    public String name() { return "ERF"; }

    public void checkConstraints(final EvolutionState state,
        final int tree,
        final GPIndividual typicalIndividual,
        final Parameter individualBase)
        {
        super.checkConstraints(state,tree,typicalIndividual,individualBase);
        // make sure we don't have any children.  This is the typical situation for an ERC.
        if (children.length!= 2) state.output.error("Incorrect number of children for the node " + toStringForError() + " (should be 0)");
        }

    public abstract void resetNode(final EvolutionState state, int thread);

    public abstract boolean nodeEquals(final GPNode node);

    public int nodeHashCode() { return super.nodeHashCode() ^ encode().hashCode(); }

    public String toStringForHumans() 
        { return toString(); }

    public String toString() 
        { return name() + "[" + encode() + "]"; }

    public abstract String encode();

    public boolean decode(final DecodeReturn dret)
        {
        return false;
        }

    public void mutateERF(final EvolutionState state, final int thread)
        {
        resetNode(state,thread);
        }

    public void writeNode(final EvolutionState state, final DataOutput dataOutput) throws IOException
        {
        state.output.fatal("writeNode(EvolutionState,DataInput) not implemented in " + getClass().getName());
        }

    public void readNode(final EvolutionState state, final DataInput dataInput) throws IOException
        {
        state.output.fatal("readNode(EvolutionState,DataInput) not implemented in " + getClass().getName());
        }

    public GPNode readNode(final DecodeReturn dret) 
        {
        int len = dret.data.length();
        int originalPos = dret.pos;
        
        // get my name
        String str2 = name() + "[";
        int len2 = str2.length();

        if (dret.pos + len2 >= len)  // uh oh, not enough space
            return null;

        // check it out
        for(int x=0; x < len2 ; x++)
            if (dret.data.charAt(dret.pos + x) != str2.charAt(x))
                return null;

        // looks good!  try to load this sucker.
        dret.pos += len2;
        ERF node = (ERF) lightClone();
        if (!node.decode(dret)) 
            { dret.pos = originalPos; return null; }  // couldn't decode it

        // the next item should be a "]"
        
        if (dret.pos >= len)
            { dret.pos = originalPos; return null; }
        if (dret.data.charAt(dret.pos) != ']') 
            { dret.pos = originalPos; return null; }
        
        // Check to make sure that the ERC's all there is
        if (dret.data.length() > dret.pos+1)
            {
            char c = dret.data.charAt(dret.pos+1);
            if (!Character.isWhitespace(c) &&
                c != ')' && c != '(') // uh oh
                { dret.pos = originalPos; return null; }
            }   

        dret.pos++;

        return node;
        }
    }