/*

 */


package ec.app.mmHH;
import ec.*;
import ec.gp.*;

public class Abs extends GPNode
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */

	public String toString() { return "abs"; }

	/*
  public void checkConstraints(final EvolutionState state,
  final int tree,
  final GPIndividual typicalIndividual,
  final Parameter individualBase)
  {
  super.checkConstraints(state,tree,typicalIndividual,individualBase);
  if (children.length!=2)
  state.output.error("Incorrect number of children for node " +
  toStringForError() + " at " +
  individualBase);
  }
	 */
	public int expectedChildren() { return 1; }

	public void eval(final EvolutionState state,
			final int thread,
			final GPData input,
			final ADFStack stack,
			final GPIndividual individual,
			final Problem problem)
	{
		double result;
		DoubleData rd = ((DoubleData)(input));

		children[0].eval(state,thread,input,stack,individual,problem);
		result = rd.x;
		rd.x = Math.abs(result);
	}

}

