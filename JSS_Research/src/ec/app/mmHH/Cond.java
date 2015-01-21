/*

 */


package ec.app.mmHH;
import ec.*;
import ec.gp.*;

public class Cond extends GPNode
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public String toString() { return "if>0"; }

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
	public int expectedChildren() { return 3; }

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

		if(result>0.0)
		{
			children[1].eval(state,thread,input,stack,individual,problem);
		}
		else
		{
			children[2].eval(state,thread,input,stack,individual,problem);
		}

	}


}

