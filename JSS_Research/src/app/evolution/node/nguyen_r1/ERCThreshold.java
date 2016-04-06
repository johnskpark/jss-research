package app.evolution.node.nguyen_r1;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import app.evolution.JasimaGPData;
import app.evolution.node.SingleLineERC;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.util.Code;
import ec.util.DecodeReturn;

public class ERCThreshold extends SingleLineERC {

	private static final long serialVersionUID = 8633100100313813834L;

	private static final double[] THRESHOLDS = new double[] {
			0.1, 0.2, 0.3, 0.4, 0.5,
			0.6, 0.7, 0.8, 0.9, 1.0
	};

	private double threshold;

	@Override
	public String toString() {
		return threshold+"";
	}

	@Override
	public void resetNode(EvolutionState state, int thread) {
		int index = state.random[thread].nextInt(THRESHOLDS.length);

		threshold = THRESHOLDS[index];
	}

	@Override
	public boolean nodeEquals(GPNode node) {
		if (node == null || node.getClass() != this.getClass()) {
			return false;
		}
		ERCThreshold other = (ERCThreshold) node;
		return this.threshold == other.threshold;
	}

	@Override
	public String encode() {
		return Code.encode(threshold);
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

		threshold = dret.d;
		return true;
	}

	@Override
	public void writeNode(final EvolutionState state,
			final DataOutput dataOutput) throws IOException {
		dataOutput.writeDouble(threshold);
	}

	@Override
	public void readNode(final EvolutionState state,
			final DataInput dataInput) throws IOException {
		threshold = dataInput.readDouble();
	}

	@Override
	public void eval(final EvolutionState state,
			final int thread,
			final GPData input,
			final ADFStack stack,
			final GPIndividual individual,
			final Problem problem) {
		JasimaGPData data = (JasimaGPData) input;

		data.setPriority(threshold);
	}

}
