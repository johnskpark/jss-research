package app.evolution.coop.statistics;

import java.util.ArrayList;

import ec.EvolutionState;
import ec.Individual;
import ec.multiobjective.MultiObjectiveFitness;
import ec.multiobjective.MultiObjectiveStatistics;
import ec.simple.SimpleProblemForm;
import ec.util.QuickSort;
import ec.util.SortComparator;

public class JasimaCoopMOStatistics extends MultiObjectiveStatistics {

	private static final long serialVersionUID = -84845920561007995L;

	@Override
	public void finalStatistics(final EvolutionState state, final int result) {
		bypassFinalStatistics(state, result);  // just call super.super.finalStatistics(...)

		// for now we just print the best fitness

		if (doFinal) { state.output.println("\nBest Individual of Run:", statisticslog); }
		for(int x = 0; x < state.population.subpops.length; x++) {
			if (doFinal) { state.output.println("Subpopulation " + x + ":", statisticslog); }

			@SuppressWarnings("rawtypes")
			ArrayList front = MultiObjectiveFitness.partitionIntoParetoFront(state.population.subpops[x].individuals, null, null);

			Object[] sortedFront = front.toArray();
			QuickSort.qsort(sortedFront, new SortComparator() {
				public boolean lt(Object a, Object b)
				{
					MultiObjectiveFitness fitnessA = (MultiObjectiveFitness) ((Individual) a).fitness;
					MultiObjectiveFitness fitnessB = (MultiObjectiveFitness) ((Individual) b).fitness;

					if (fitnessA.isMaximizing(0)) {
						return fitnessA.getObjective(0) > fitnessB.getObjective(0);
					} else {
						return fitnessA.getObjective(0) < fitnessB.getObjective(0);
					}
				}

				public boolean gt(Object a, Object b)
				{
					MultiObjectiveFitness fitnessA = (MultiObjectiveFitness) ((Individual) a).fitness;
					MultiObjectiveFitness fitnessB = (MultiObjectiveFitness) ((Individual) b).fitness;

					if (fitnessA.isMaximizing(0)) {
						return fitnessA.getObjective(0) < fitnessB.getObjective(0);
					} else {
						return fitnessA.getObjective(0) > fitnessB.getObjective(0);
					}
				}
			});

			Individual bestFirstObjOfRun = (Individual) sortedFront[0];

			if (doFinal) { bestFirstObjOfRun.printIndividualForHumans(state,statisticslog); }
			if (doMessage && !silentPrint) { state.output.message("Subpop " + x + " best first objective fitness of run: " + bestFirstObjOfRun.fitness.fitnessToStringForHumans()); }

			// finally describe the winner if there is a description
			if (doFinal && doDescription) {
				if (state.evaluator.p_problem instanceof SimpleProblemForm) {
					((SimpleProblemForm) (state.evaluator.p_problem.clone())).describe(state, bestFirstObjOfRun, x, 0, statisticslog);
				}
			}
		}
	}
}
