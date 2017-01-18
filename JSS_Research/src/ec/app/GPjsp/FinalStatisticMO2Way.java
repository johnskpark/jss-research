/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.app.GPjsp;

import ec.EvolutionState;
import ec.Individual;
import ec.multiobjective.MultiObjectiveFitness;
import ec.util.QuickSort;
import ec.util.SortComparator;
import java.util.ArrayList;

/**
 *
 * @author nguyensu
 */
public class FinalStatisticMO2Way {
    private static int statisticslog;
    public static String TestResult = "";
    private static int frontLog;
public static void myFinalStatistic(final EvolutionState state, final int result, GPjsp2WayMO gp, int threadnum, int startIndex, int nInstances)
        {
        // super.finalStatistics(state,result);
        // I don't want just a single best fitness

        state.output.println("\n\n\n PARETO FRONTS", statisticslog);
        for (int s = 0; s < state.population.subpops.length; s++)
            {
            MultiObjectiveFitness typicalFitness = (MultiObjectiveFitness)(state.population.subpops[s].individuals[0].fitness);
            state.output.println("\n\nPareto Front of Subpopulation " + s, statisticslog);

            // build front
            ArrayList front = typicalFitness.partitionIntoParetoFront(state.population.subpops[s].individuals, null, null);

            // sort by objective[0]
            Object[] sortedFront = front.toArray();
            QuickSort.qsort(sortedFront, new SortComparator()
                {
                public boolean lt(Object a, Object b)
                    {
                    return (((MultiObjectiveFitness) (((Individual) a).fitness)).getObjective(0) <
                        (((MultiObjectiveFitness) ((Individual) b).fitness)).getObjective(0));
                    }

                public boolean gt(Object a, Object b)
                    {
                    return (((MultiObjectiveFitness) (((Individual) a).fitness)).getObjective(0) >
                        ((MultiObjectiveFitness) (((Individual) b).fitness)).getObjective(0));
                    }
                });

            // print out header
            state.output.message("Pareto Front Summary: " + sortedFront.length + " Individuals");
            String message = "Ind";
            int numObjectives = typicalFitness.getObjectives().length;
            for(int i = 0; i < numObjectives; i++)
                message += ("\t" + "Objective " + i);
            String[] names = typicalFitness.getAuxilliaryFitnessNames();
            for(int i = 0; i < names.length; i++)
                message += ("\t" + names[i]);
            state.output.message(message);

            // write front to screen
            for (int i = 0; i < sortedFront.length; i++)
                {
                Individual individual = (Individual) (sortedFront[i]);

                double[] objectives = ((MultiObjectiveFitness) individual.fitness).getObjectives();
                String line = "" + i;
                for (int f = 0; f < objectives.length; f++)
                    line += ("\t" + objectives[f]);

                double[] vals = ((MultiObjectiveFitness) individual.fitness).getAuxilliaryFitnessValues();
                for(int f = 0; f < vals.length; f++)
                    line += ("\t" + vals[f]);
                //for testing (not in origial ECJ
                TestResult+= "\n" + gp.getTestPerformance(state, threadnum, individual, startIndex, nInstances);
                ////////////////////////////////
                state.output.message(line);
                }

            // print out front to statistics log
            for (int i = 0; i < sortedFront.length; i++)
                ((Individual)(sortedFront[i])).printIndividualForHumans(state, statisticslog);

            // write short version of front out to disk
            if (frontLog >= 0)
                {
                if (state.population.subpops.length > 1)
                    state.output.println("Subpopulation " + s, frontLog);
                for (int i = 0; i < sortedFront.length; i++)
                    {
                    Individual ind = (Individual)(sortedFront[i]);
                    MultiObjectiveFitness mof = (MultiObjectiveFitness) (ind.fitness);
                    double[] objectives = mof.getObjectives();

                    String line = "";
                    for (int f = 0; f < objectives.length; f++)
                        line += (objectives[f] + " ");
                    state.output.println(line, frontLog);
                    }
                }
            }
            state.output.println("TestSet Result", frontLog);
            state.output.println(TestResult, frontLog);
        }
}
