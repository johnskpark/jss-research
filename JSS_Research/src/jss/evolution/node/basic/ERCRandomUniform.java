package jss.evolution.node.basic;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import jss.evolution.JSSGPData;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.ERC;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.util.Code;
import ec.util.DecodeReturn;
import ec.util.Parameter;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class ERCRandomUniform extends ERC {

	private static final long serialVersionUID = 3518496461314357556L;

	public static final String P_MIN = "min";
	public static final String P_MAX = "max";

	private double minVal = Double.NaN;
	private double maxVal = Double.NaN;

	private double value;

	@Override
	public String toString() {
		return value+"";
	}

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		try {
			minVal = state.parameters.getDouble(base.push(P_MIN), null);
			maxVal = state.parameters.getDouble(base.push(P_MAX), null);
		} catch (NumberFormatException ex) {
			state.output.fatal(ex.getMessage());
		}
	}

	@Override
	public void resetNode(final EvolutionState state, int thread) {
		try {
			if (Double.isNaN(minVal) || Double.isNaN(maxVal)) {
				throw new NumberFormatException();
			}

			value = state.random[thread].nextDouble() *
					(maxVal - minVal) + minVal;
		} catch (NumberFormatException ex) {
			state.output.fatal("Insufficient parameters for ERCRandomUniform, must define the interval.");
		}
	}

	@Override
	public boolean nodeEquals(final GPNode node) {
		if (node == null || node.getClass() != this.getClass()) {
			return false;
		}
		ERCRandomUniform other = (ERCRandomUniform)node;
		return this.value == other.value;
	}

	@Override
	public String encode() {
		return Code.encode(value);
	}

	@Override
	public boolean decode(final DecodeReturn dret) {
		int pos = dret.pos;
		String data = dret.data;

		Code.decode(dret);

		if (dret.type != DecodeReturn.T_DOUBLE) {
			dret.data = data;
			dret.pos = pos;
			return false;
		}

		value = dret.d;
		return true;
	}

	@Override
	public void writeNode(final EvolutionState state,
			final DataOutput dataOutput) throws IOException {
		dataOutput.writeDouble(value);
	}

	@Override
	public void readNode(final EvolutionState state,
			final DataInput dataInput) throws IOException {
		value = dataInput.readDouble();
	}

	@Override
	public void eval(final EvolutionState state,
			final int thread,
			final GPData input,
			final ADFStack stack,
			final GPIndividual individual,
			final Problem problem) {
		JSSGPData data = (JSSGPData)input;

		data.setPriority(value);
	}


}
