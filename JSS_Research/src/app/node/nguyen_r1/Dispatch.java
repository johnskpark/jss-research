package app.node.nguyen_r1;

import java.util.ArrayList;
import java.util.List;

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
import ec.util.ParamClassLoadException;
import ec.util.Parameter;
import jasima.shopSim.core.PR;

// TODO doesn't carry out beforeCalc.
public class Dispatch extends SingleLineERC {

	private static final long serialVersionUID = 8547299423664015016L;

	public static final String P_NUM = "num-rule";
	public static final String P_RULE = "rule";

	private List<PR> dispatchingRules = new ArrayList<PR>();
	private int numRules;

	private PR selectedRule;
	private int selectedIndex;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		try {
			numRules = state.parameters.getInt(base.push(P_NUM), null);

			for (int i = 0; i < numRules; i++) {
				PR rule = (PR) state.parameters.getInstanceForParameterEq(base.push(P_RULE).push(i+""), base.push(P_RULE), PR.class);

				dispatchingRules.add(rule);
			}
		} catch (ParamClassLoadException ex) {
			state.output.fatal(ex.getMessage());
		}
	}

	@Override
	public void resetNode(EvolutionState state, int thread) {
		selectedIndex = state.random[thread].nextInt(numRules);
		selectedRule = dispatchingRules.get(selectedIndex);
	}

	@Override
	public boolean nodeEquals(GPNode node) {
		if (node == null || node.getClass() != this.getClass()) {
			return false;
		}
		Dispatch other = (Dispatch) node;
		return this.selectedRule.equals(other.selectedRule);
	}

	@Override
	public String encode() {
		return Code.encode(selectedIndex);
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

		selectedIndex = (int) dret.d;
		selectedRule = dispatchingRules.get(selectedIndex);
		return true;
	}

	@Override
	public void eval(final EvolutionState state,
			final int thread,
			final GPData input,
			final ADFStack stack,
			final GPIndividual individual,
			final Problem problem) {
		JasimaGPData data = (JasimaGPData) input;

		data.setPriority(selectedRule.calcPrio(data.getPrioRuleTarget()));
	}

}
