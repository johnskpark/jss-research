package app.evolution.pickardt;

import jasima.shopSim.core.PR;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import app.node.CompositePR;
import app.node.INode;
import app.util.RuleParser;
import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;

// TODO Hmmm right, how do I want to approach this.
// I need a two step process, and with that, I need to override EvolutionState to take two steps.
// Hmmm, what is the best way to carry out this two step procedure?

// You know what? Fuck it, I'm going to just run the damn thing one after the another.
//
public class JasimaMetaProblem extends Problem implements SimpleProblemForm {

	public static final String P_GPRULE = "ruleFile";

	private RuleParser parser = new RuleParser();
	private PR gpRule;

	// TODO
	private PR[] presetRules = new PR[]{
			// PRFCFS, PRCR, WSPT, WMDD, WMOD, CR, SPT, LWKR, EDD, MDD, ODD, MOD, SLACK,
			// ATCS, BATCS (k1=2.0,3.0,5.0,6.0, k2=0.005,0.01,0.1,1.0)
	};

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		try {
			File file = new File(state.parameters.getString(base.push(P_GPRULE), null));
			FileInputStream fileInputStream = new FileInputStream(file);

			byte[] buffer = new byte[(int)file.length()];
			fileInputStream.read(buffer);
			fileInputStream.close();

			gpRule = new CompositePR(parser.getRuleFromString(new String(buffer, "UTF-8")));
		} catch (IOException ex) {
			state.output.fatal("Error while reading GP rule file " + ex.getMessage());
		}
	}

	@Override
	public void evaluate(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		// TODO Auto-generated method stub

	}

}
