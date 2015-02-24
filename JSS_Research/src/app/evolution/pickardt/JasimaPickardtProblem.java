package app.evolution.pickardt;

import jasima.shopSim.core.PR;
import jasima.shopSim.prioRules.basic.CR;
import jasima.shopSim.prioRules.basic.EDD;
import jasima.shopSim.prioRules.basic.MDD;
import jasima.shopSim.prioRules.basic.MOD;
import jasima.shopSim.prioRules.basic.ODD;
import jasima.shopSim.prioRules.basic.SLK;
import jasima.shopSim.prioRules.basic.SPT;
import jasima.shopSim.prioRules.basic.SRPT;
import jasima.shopSim.prioRules.batch.BATCS;
import jasima.shopSim.prioRules.setup.ATCS;
import jasima.shopSim.prioRules.weighted.WMDD;
import jasima.shopSim.prioRules.weighted.WMOD;
import jasima.shopSim.prioRules.weighted.WSPT;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import app.evolution.pickardt.presetRules.PRCR;
import app.evolution.pickardt.presetRules.PRFCFS;
import app.node.CompositePR;
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
public class JasimaPickardtProblem extends Problem implements SimpleProblemForm {

	private static final long serialVersionUID = 4598953667123971268L;

	public static final String P_GPRULE = "ruleFile";

	private RuleParser parser = new RuleParser();

	private PR[] rules = new PR[]{
			// PRFCFS, PRCR, WSPT, WMDD, WMOD, CR, SPT, LWKR, EDD, MDD, ODD, MOD, SLACK,
			// ATCS, BATCS (k1=2.0,3.0,5.0,6.0, k2=0.005,0.01,0.1,1.0)
			null, // placeholder for GP rule.
			new PRFCFS(),
			new PRCR(),
			new WSPT(),
			new WMDD(),
			new WMOD(),
			new CR(),
			new SPT(),
			new SRPT(),
			new EDD(),
			new MDD(),
			new ODD(),
			new MOD(),
			new SLK(),
			new ATCS(2.0, 0.005),
			new ATCS(3.0, 0.01),
			new ATCS(5.0, 0.1),
			new ATCS(6.0, 1.0),
			new BATCS(2.0, 0.005),
			new BATCS(3.0, 0.01),
			new BATCS(5.0, 0.1),
			new BATCS(6.0, 1.0)
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

			rules[0] = new CompositePR(parser.getRuleFromString(new String(buffer, "UTF-8")));
		} catch (IOException ex) {
			state.output.fatal("Error while reading GP rule file " + ex.getMessage());
		}
	}

	@Override
	public void prepareToEvaluate(final EvolutionState state, final int threadnum) {
		Individual[] inds = state.population.subpops[0].individuals;

		for (int i = 0; i < inds.length; i++) {
			// TODO
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
