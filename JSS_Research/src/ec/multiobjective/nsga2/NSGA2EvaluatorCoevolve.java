/*
  Copyright 2010 by Sean Luke and George Mason University
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package ec.multiobjective.nsga2;

import java.util.*;
import ec.*;
import ec.coevolve.MultiPopCoevolutionaryEvaluatorMO;
import ec.gp.GPIndividual;
import ec.multiobjective.*;
import ec.simple.*;
import ec.util.*;

/* 
 * NSGA2Evaluator.java
 * 
 * Created: Sat Oct 16 00:19:57 EDT 2010
 * By: Faisal Abidi and Sean Luke
 */


/**
 * The NSGA2Evaluator is a simple generational evaluator which
 * evaluates every single member of the population (in a multithreaded fashion).
 * Then it reduces the population size to an <i>archive</i> consisting of the
 * best front ranks.  When there isn't enough space to fit another front rank,
 * individuals in that final front rank vie for the remaining slots in the archive
 * based on their sparsity.
 * 
 * <p>The evaluator is also responsible for calculating the rank and
 * sparsity values stored in the NSGA2MultiObjectiveFitness class and used largely
 * for statistical information.
 * 
 * <p>NSGA-II has fixed archive size (the population size), and so ignores the 'elites'
 * declaration.  However it will adhere to the 'reevaluate-elites' parameter in SimpleBreeder
 * to determine whether to force fitness reevaluation.
 *
 */
 
public class NSGA2EvaluatorCoevolve extends MultiPopCoevolutionaryEvaluatorMO
    {
    /** The original population size is stored here so NSGA2 knows how large to create the archive
        (it's the size of the original population -- keep in mind that NSGA2Breeder had made the 
        population larger to include the children. */
    public int originalPopSize[];
    
    public int archiveSize = 200;
            
    public Individual[] archive;

    public void setup(final EvolutionState state, final Parameter base)
        {
        super.setup(state, base);
        Parameter p = new Parameter(Initializer.P_POP);
        int subpopsLength = state.parameters.getInt(p.push(Population.P_SIZE), null, 1);
        Parameter p_subpop;
        originalPopSize = new int[subpopsLength];
        for (int i = 0; i < subpopsLength; i++)
            {
            p_subpop = p.push(Population.P_SUBPOP).push("" + i).push(Subpopulation.P_SUBPOPSIZE);
            originalPopSize[i] = state.parameters.getInt(p_subpop, null, 1);
            }
        }


    /**
     * Evaluates the population, then builds the archive and reduces the population to just the archive.
     */
    public void evaluatePopulation(final EvolutionState state)
        {
        super.evaluatePopulation(state);
        Individual[] combinedInds;
        Individual[] sub1 = state.population.subpops[0].individuals;
        Individual[] sub2 = state.population.subpops[1].individuals;
        Individual[] sub3 = state.population.archive;
        combinedInds = new Individual[sub1.length + sub2.length + sub3.length];
        System.arraycopy(sub1, 0, combinedInds, 0,  sub1.length);
        System.arraycopy(sub2, 0, combinedInds,  sub1.length, sub2.length);
        System.arraycopy(sub3, 0, combinedInds,  sub1.length + sub2.length, sub3.length);
        state.population.archive = buildArchive(combinedInds);
        afterCoevolutionaryEvaluation(state);
        }


    /** Build the auxiliary fitness data and reduce the subpopulation to just the archive, which is
        returned. */
    public Individual[] buildArchive(Individual[] Inds)
        {
        Individual[] dummy = new Individual[0];
               
        ArrayList ranks = assignFrontRanks(Inds);
                
        int size = ranks.size();
        for(int i = 0; i < size; i++)
        {
            Individual[] rank = (Individual[])((ArrayList)(ranks.get(i))).toArray(dummy);
            assignSparsity(rank);
        }
        
        Individual[] newInds  = (Individual[])((ArrayList)(ranks.get(0))).toArray(dummy);
        if (newInds.length >= archiveSize)
            {
            // first sort the rank by sparsity
            ec.util.QuickSort.qsort(newInds, new SortComparator()
                {
                public boolean lt(Object a, Object b)
                    {
                    Individual i1 = (Individual) a;
                    Individual i2 = (Individual) b;
                    return (((NSGA2MultiObjectiveFitness) i1.fitness).sparsity > ((NSGA2MultiObjectiveFitness) i2.fitness).sparsity);
                    }

                public boolean gt(Object a, Object b)
                    {
                    Individual i1 = (Individual) a;
                    Individual i2 = (Individual) b;
                    return (((NSGA2MultiObjectiveFitness) i1.fitness).sparsity < ((NSGA2MultiObjectiveFitness) i2.fitness).sparsity);
                    }
                });

            // then put the m sparsest individuals in the new population
            archive = new Individual[archiveSize];
            for(int j = 0 ; j < archiveSize; j++){
                archive[j] = (Individual) newInds[j].clone();
            }
        } else{
            archive = new Individual[newInds.length];
            for (int i = 0; i < newInds.length; i++) {   
                archive[i] = (Individual) newInds[i].clone();  
            }
        }
            
        

        System.out.println("** archive size = " + archive.length + " **");
        return archive;
        }



    /** Divides inds into ranks and assigns each individual's rank to be the rank it was placed into.
        Each front is an ArrayList. */
    public ArrayList assignFrontRanks(Subpopulation subpop)
        {
        Individual[] inds = subpop.individuals;
        ArrayList frontsByRank = MultiObjectiveFitness.partitionIntoRanks(inds);

        int numRanks = frontsByRank.size();
        for(int rank = 0; rank < numRanks; rank++)
            {
            ArrayList front = (ArrayList)(frontsByRank.get(rank));
            int numInds = front.size();
            for(int ind = 0; ind < numInds; ind++)
                ((NSGA2MultiObjectiveFitness)(((Individual)(front.get(ind))).fitness)).rank = rank;
            }
        return frontsByRank;
        }
    public ArrayList assignFrontRanks(Individual[] inds)
        {
        ArrayList frontsByRank = MultiObjectiveFitness.partitionIntoRanks(inds);

        int numRanks = frontsByRank.size();
        for(int rank = 0; rank < numRanks; rank++)
            {
            ArrayList front = (ArrayList)(frontsByRank.get(rank));
            int numInds = front.size();
            for(int ind = 0; ind < numInds; ind++)
                ((NSGA2MultiObjectiveFitness)(((Individual)(front.get(ind))).fitness)).rank = rank;
            }
        return frontsByRank;
        }


    /**
     * Computes and assigns the sparsity values of a given front.
     */
    public void assignSparsity(Individual[] front)
        {
        int numObjectives = ((NSGA2MultiObjectiveFitness) front[0].fitness).getObjectives().length;
                
        for (int i = 0; i < front.length; i++)
            ((NSGA2MultiObjectiveFitness) front[i].fitness).sparsity = 0;

        for (int i = 0; i < numObjectives; i++)
            {
            final int o = i;
            // 1. Sort front by each objective.
            // 2. Sum the manhattan distance of an individual's neighbours over
            // each objective.
            // NOTE: No matter which objectives objective you sort by, the
            // first and last individuals will always be the same (they maybe
            // interchanged though). This is because a Pareto front's
            // objective values are strictly increasing/decreasing.
            ec.util.QuickSort.qsort(front, new SortComparator()
                {
                public boolean lt(Object a, Object b)
                    {
                    Individual i1 = (Individual) a;
                    Individual i2 = (Individual) b;
                    return (((NSGA2MultiObjectiveFitness) i1.fitness).getObjective(o) < ((NSGA2MultiObjectiveFitness) i2.fitness).getObjective(o));
                    }

                public boolean gt(Object a, Object b)
                    {
                    Individual i1 = (Individual) a;
                    Individual i2 = (Individual) b;
                    return (((NSGA2MultiObjectiveFitness) i1.fitness).getObjective(o) > ((NSGA2MultiObjectiveFitness) i2.fitness).getObjective(o));
                    }
                });

            // Compute and assign sparsity.
            // the first and last individuals are the sparsest.
            ((NSGA2MultiObjectiveFitness) front[0].fitness).sparsity = Double.POSITIVE_INFINITY;
            ((NSGA2MultiObjectiveFitness) front[front.length - 1].fitness).sparsity = Double.POSITIVE_INFINITY;
            ((NSGA2MultiObjectiveFitness) front[0].fitness).size = ((GPIndividual)front[0]).size();
            ((NSGA2MultiObjectiveFitness) front[front.length - 1].fitness).size = ((GPIndividual)front[front.length - 1]).size();
            double min = ((NSGA2MultiObjectiveFitness) front[0].fitness).getObjective(o);
            double max = ((NSGA2MultiObjectiveFitness) front[front.length - 1].fitness).getObjective(o);
            for (int j = 1; j < front.length - 1; j++)
                {
                NSGA2MultiObjectiveFitness f_j = (NSGA2MultiObjectiveFitness) (front[j].fitness);
                NSGA2MultiObjectiveFitness f_jplus1 = (NSGA2MultiObjectiveFitness) (front[j+1].fitness);
                NSGA2MultiObjectiveFitness f_jminus1 = (NSGA2MultiObjectiveFitness) (front[j-1].fitness);
                                
                // store the NSGA2Sparsity in sparsity
                f_j.sparsity += (f_jplus1.getObjective(o) - f_jminus1.getObjective(o)) / (max - min);
                f_j.size = ((GPIndividual)front[j]).size();
                }
            }
        }
    }