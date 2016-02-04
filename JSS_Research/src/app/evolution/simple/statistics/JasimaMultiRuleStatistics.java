package app.evolution.simple.statistics;

import java.util.HashSet;
import java.util.Set;

import app.evolution.simple.JasimaSimpleStatistics;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;
import ec.util.QuickSort;
import ec.util.SortComparator;

public class JasimaMultiRuleStatistics extends JasimaSimpleStatistics {

	private static final long serialVersionUID = -5301164875797180565L;

	public static final String P_NUM_PRINTED = "size";

	private int numPrinted;

	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		try {
			numPrinted = state.parameters.getInt(base.push(P_NUM_PRINTED), null);
		} catch (NumberFormatException ex) {
			state.output.fatal(ex.getMessage());
		}
	}

	@Override
	public void finalStatistics(final EvolutionState state, final int result) {
		bypassFinalStatistics(state, result);  // just call super.super.finalStatistics(...)

		// for now we just print the best fitness

		if (doFinal) { state.output.println("\nBest Individual of Run:", statisticslog); }
		for(int s = 0; s < state.population.subpops.length; s++) {
			if (doFinal) { state.output.println("Subpopulation " + s + ":", statisticslog); }
			Object[] inds = state.population.subpops[0].individuals;

			QuickSort.qsort(inds, new SortComparator() {
				public boolean lt(Object a, Object b)
				{
					KozaFitness fitnessA = (KozaFitness) ((Individual) a).fitness;
					KozaFitness fitnessB = (KozaFitness) ((Individual) b).fitness;

					return fitnessA.betterThan(fitnessB);
				}

				public boolean gt(Object a, Object b)
				{
					KozaFitness fitnessA = (KozaFitness) ((Individual) a).fitness;
					KozaFitness fitnessB = (KozaFitness) ((Individual) b).fitness;

					return fitnessB.betterThan(fitnessA);
				}
			});

			Set<Individual> indSet = new HashSet<Individual>();
			for (int i = 0; i < numPrinted && i < inds.length; i++) {
				Individual ind = (Individual) inds[i];
				if (indSet.contains(ind)) {
					continue;
				}
				indSet.add(ind);

				if (doFinal) { ind.printIndividualForHumans(state, statisticslog); }
				if (doMessage && !silentPrint) {
					state.output.message("Subpop " + s + " number " + (indSet.size() - 1) + " objective fitness of run: " + ind.fitness.fitnessToStringForHumans());
				}

				// finally describe the winner if there is a description
				if (doFinal && doDescription) {
					if (state.evaluator.p_problem instanceof SimpleProblemForm) {
						((SimpleProblemForm) (state.evaluator.p_problem.clone())).describe(state, ind, s, 0, statisticslog);
					}
				}
			}

		}
	}
}
