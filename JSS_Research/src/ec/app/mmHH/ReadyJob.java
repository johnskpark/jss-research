/*
  2013 Rachel Hunt
  Terminal for GP: RJ - returns the ready time of the operation.
 */


package ec.app.mmHH;
import ec.*;
import ec.gp.*;

public class ReadyJob extends GPNode
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public String toString() { return "RJ"; }

	/*
  public void checkConstraints(final EvolutionState state,
  final int tree,
  final GPIndividual typicalIndividual,
  final Parameter individualBase)
  {
  super.checkConstraints(state,tree,typicalIndividual,individualBase);
  if (children.length!=0)
  state.output.error("Incorrect number of children for node " +
  toStringForError() + " at " +
  individualBase);
  }
	 */
	public int expectedChildren() { return 0; }

	public void eval(final EvolutionState state,
			final int thread,
			final GPData input,
			final ADFStack stack,
			final GPIndividual individual,
			final Problem problem)
	{
		DoubleData rd = ((DoubleData)(input));
		rd.x = ((MultiMachineDJS)problem).currentJob.getRJ();
	}

}




