package app.evolution.node;

import ec.EvolutionState;
import ec.gp.GPNode;

/**
 * An abstract intermediate class to get rid of the stupid newline and tabbing
 * that GPNode class does.
 *
 * @author parkjohn
 *
 */
public abstract class SingleLineGPNode extends GPNode {

	private static final long serialVersionUID = -9209286914050846080L;

	@Override
	public int printRootedTreeForHumans(final EvolutionState state,
			final int log,
			final int verbosity,
			int tablevel,
			int printbytes) {
		if (children.length > 0) {
			state.output.print(" (",log);
			printbytes += 2;
		} else {
			state.output.print(" ",log);
			printbytes += 1;
		}

		printbytes += printNodeForHumans(state,log);

		for (int x=0;x<children.length;x++) {
			printbytes = children[x].printRootedTreeForHumans(state,log,tablevel,printbytes);
		}
		if (children.length>0) {
			state.output.print(")",log);
			printbytes += 1;
		}
		return printbytes;
	}
}
