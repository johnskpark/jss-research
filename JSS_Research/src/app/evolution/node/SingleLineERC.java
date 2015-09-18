package app.evolution.node;

import ec.EvolutionState;
import ec.gp.ERC;

/**
 * An abstract intermediate class to get rid of the stupid newline and tabbing
 * that GPNode class does.
 *
 * @author parkjohn
 *
 */
public abstract class SingleLineERC extends ERC {

	private static final long serialVersionUID = 9037502341187473494L;

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
