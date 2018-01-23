package app.evolution.niched;

import ec.EvolutionState;
import ec.Individual;
import ec.Subpopulation;
import ec.simple.SimpleProblemForm;
import ec.simple.SimpleStatistics;
import ec.util.Parameter;

public class JasimaNichedStatistics extends SimpleStatistics {

	private static final long serialVersionUID = -2627954270593351497L;

	private Individual bestIndOfGen[];

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// Left blank for now, fill in if necessary later.
	}

	@Override
	public void preEvaluationStatistics(final EvolutionState state) {
		super.preEvaluationStatistics(state);

		bestIndOfGen = new Individual[state.population.subpops.length];
	}

	@Override
    public void postEvaluationStatistics(final EvolutionState state) {
		super.postEvaluationStatistics(state);

		// Get the best individual of the current generation.
		for (int i = 0; i < state.population.subpops.length; i++) {
			Subpopulation subpop = state.population.subpops[i];
			for (int j = 0; j < subpop.individuals.length; j++) {
				Individual ind = subpop.individuals[j];

				if (ind.evaluated) {
					if (bestIndOfGen[i] == null || ind.fitness.betterThan(bestIndOfGen[i].fitness)) {
						bestIndOfGen[i] = ind;
					}
				}
			}
		}

		// Print out the niched individuals of the current generation.
        if (doGeneration) { state.output.println("Best Niched Individual:",statisticslog); }
        for (int i = 0; i < state.population.archive.length; i++) {
        	Individual nichedInd = state.population.archive[i];
        	if (doGeneration) {
        		state.output.println("Niche " + i + ":", statisticslog);
        		nichedInd.printIndividualForHumans(state, statisticslog);
    		}

        	if (doMessage && !silentPrint) {
        		if (nichedInd.evaluated) {
        			state.output.message("Niche " + i + " best fitness of generation " + nichedInd.fitness.fitnessToStringForHumans());
        		} else {
        			state.output.message("Niche " + i + " best fitness of generation (evaluated flag not set): " + nichedInd.fitness.fitnessToStringForHumans());
        		}
        	}

        	if (doGeneration && doPerGenerationDescription) {
                if (state.evaluator.p_problem instanceof SimpleProblemForm) {
                    ((SimpleProblemForm) (state.evaluator.p_problem.clone())).describe(state, nichedInd, i, 0, statisticslog);
                }
        	}
        }
    }


	@Override
    public void finalStatistics(final EvolutionState state, final int result) {
		bypassFinalStatistics(state, result);

		// Print the best fitness of last generation of subpopulation i.
		if (doFinal) { state.output.println("\nBest Individual of Run:", statisticslog); }
		for (int i = 0; i < state.population.subpops.length; i++) {
			if (doFinal) {
				state.output.println("Subpopulation " + i + ":", statisticslog);
				bestIndOfGen[i].printIndividualForHumans(state, statisticslog);
			}

			if (doMessage && !silentPrint) { state.output.message("Subpop " + i + " best fitness of run: " + bestIndOfGen[i].fitness.fitnessToStringForHumans()); }
			// finally describe the winner if there is a description
			if (doFinal && doDescription) {
				if (state.evaluator.p_problem instanceof SimpleProblemForm) {
					((SimpleProblemForm) (state.evaluator.p_problem.clone())).describe(state, bestIndOfGen[i], 0, 0, statisticslog);
				}
			}
		}

		// Print the niched individual of the last generation.
        if (doFinal) { state.output.println("\nBest Niched Individual:",statisticslog); }
        for (int i = 0; i < state.population.archive.length; i++) {
        	Individual nichedInd = state.population.archive[i];
        	if (doFinal) {
        		state.output.println("Niche " + i + ":", statisticslog);
        		nichedInd.printIndividualForHumans(state, statisticslog);
    		}

        	if (doMessage && !silentPrint) { state.output.message("Niche " + i + " best fitness of run " + nichedInd.fitness.fitnessToStringForHumans()); }

        	if (doFinal && doPerGenerationDescription) {
                if (state.evaluator.p_problem instanceof SimpleProblemForm) {
                    ((SimpleProblemForm) (state.evaluator.p_problem.clone())).describe(state, nichedInd, i, 0, statisticslog);
                }
        	}
        }
    }

}
