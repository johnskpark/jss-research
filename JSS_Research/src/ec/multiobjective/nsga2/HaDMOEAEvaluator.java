/*
  Copyright 2010 by Sean Luke and George Mason University
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package ec.multiobjective.nsga2;

import java.util.*;
import ec.*;
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

public class HaDMOEAEvaluator extends SimpleEvaluator
    {
    /** The original population size is stored here so NSGA2 knows how large to create the archive
        (it's the size of the original population -- keep in mind that NSGA2Breeder had made the
        population larger to include the children. */
    public int originalPopSize[];

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
        for (int x = 0; x < state.population.subpops.length; x++)
            state.population.subpops[x].individuals =
                buildArchive(state, x);
        }


    /** Build the auxiliary fitness data and reduce the subpopulation to just the archive, which is
        returned. */
    public Individual[] buildArchive(EvolutionState state, int subpop)
        {
        Individual[] dummy = new Individual[0];
        ArrayList ranks = assignFrontRanks(state.population.subpops[subpop]);

        ArrayList newSubpopulation = new ArrayList();
        int size = ranks.size();
        for(int i = 0; i < size; i++)
            {
            Individual[] rank = (Individual[])((ArrayList)(ranks.get(i))).toArray(dummy);

            if (rank.length + newSubpopulation.size() >= originalPopSize[subpop])
                {
                ArrayList temp = new ArrayList();
                for (int j = 0; j < newSubpopulation.size(); j++) {
                    temp.add(newSubpopulation.get(j));
                }
                temp.addAll(Arrays.asList(rank));

                Individual[] tempInds = (Individual[])((ArrayList)(temp)).toArray(dummy);
                assignSparsity(state,tempInds);
                // first sort the rank by sparsity
                ec.util.QuickSort.qsort(rank, new SortComparator()
                    {
                    public boolean lt(Object a, Object b)
                        {
                        Individual i1 = (Individual) a;
                        Individual i2 = (Individual) b;
                        return (((HaDMOEA2MultiObjectiveFitness) i1.fitness).sparsity > ((HaDMOEA2MultiObjectiveFitness) i2.fitness).sparsity);
                        }

                    public boolean gt(Object a, Object b)
                        {
                        Individual i1 = (Individual) a;
                        Individual i2 = (Individual) b;
                        return (((HaDMOEA2MultiObjectiveFitness) i1.fitness).sparsity < ((HaDMOEA2MultiObjectiveFitness) i2.fitness).sparsity);
                        }
                    });

                // then put the m sparsest individuals in the new population
                int m = originalPopSize[subpop] - newSubpopulation.size();
                for(int j = 0 ; j < m; j++)
                    newSubpopulation.add(rank[j]);

                // and bail
                break;
                }
            else
                {
                // dump in everyone
                for(int j = 0 ; j < rank.length; j++)
                    newSubpopulation.add(rank[j]);
                }
            }

        Individual[] archive = (Individual[])(newSubpopulation.toArray(dummy));

        // maybe force reevaluation
        HaDMOEABreeder breeder = (HaDMOEABreeder)(state.breeder);
        if (breeder.reevaluateElites[subpop])
            for(int i = 0 ; i < archive.length; i++)
                archive[i].evaluated = false;

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
                ((HaDMOEA2MultiObjectiveFitness)(((Individual)(front.get(ind))).fitness)).rank = rank;
            }
        return frontsByRank;
        }



    /**
     * Computes and assigns the sparsity values of a given front.
     */
    public void assignSparsity(EvolutionState state,Individual[] inds)
        {
        double[][] distances = calculateDistances(inds);
        // calculate k value
        int kTH = (int) Math.sqrt(inds.length);  // note that the first element is k=1, not k=0
        for (int i = 0; i < inds.length; i++){
                ((HaDMOEA2MultiObjectiveFitness) inds[i].fitness).sparsity = 0;
                double harmonicCD = 0;
                for (int j = 0; j < kTH; j++) {
                    double distance_j = Math.sqrt(orderStatistics(distances[i], j+2, state.random[0]));
                    if (distance_j==0) {
                        harmonicCD=Double.POSITIVE_INFINITY;
                        break;
                    }
                    else harmonicCD += 1/distance_j;
                }
                ((HaDMOEA2MultiObjectiveFitness) inds[i].fitness).sparsity = (double)kTH/harmonicCD;
            }
        }
        /** Returns a matrix of sum squared distances from each individual to each other individual. */
    public double[][] calculateDistances(Individual[] inds)
        {
        double[][] distances = new double[inds.length][inds.length];
        int nObj = ((HaDMOEA2MultiObjectiveFitness)inds[0].fitness).getNumObjectives();
        double[] min = new double[nObj];
        double[] max = new double[nObj];
        double[] range = new double[nObj];
        for (int i = 0; i < nObj; i++) {
            min[i] = Double.POSITIVE_INFINITY;
            max[i] = Double.NEGATIVE_INFINITY;
        }
        for (int i = 0; i < inds.length; i++) {
            for (int j = 0; j < nObj; j++) {
                if (((HaDMOEA2MultiObjectiveFitness)inds[i].fitness).getObjective(j)<min[j]) min[j] = ((HaDMOEA2MultiObjectiveFitness)inds[i].fitness).getObjective(j);
                if (((HaDMOEA2MultiObjectiveFitness)inds[i].fitness).getObjective(j)>max[j]) max[j] = ((HaDMOEA2MultiObjectiveFitness)inds[i].fitness).getObjective(j);
            }
        }
        for (int i = 0; i < nObj; i++) {
            range[i] = max[i] - min[i];
        }
        for(int y=0;y<inds.length;y++)
            {
            distances[y][y] = 0;
            for(int z=y+1;z<inds.length;z++)
                {
                distances[z][y] = distances[y][z] =
                    ((HaDMOEA2MultiObjectiveFitness) inds[y].fitness).
                    sumSquaredObjectiveDistance_normalised((HaDMOEA2MultiObjectiveFitness) inds[z].fitness, range);
                }
            }
        return distances;
        }
    /** Returns the kth smallest element in the array.  Note that here k=1 means the smallest element in the array (not k=0).
        Uses a randomized sorting technique, hence the need for the random number generator. */
    double orderStatistics(double[] array, int kth, MersenneTwisterFast rng)
        {
        return randomizedSelect(array, 0, array.length-1, kth, rng);
        }


    /* OrderStatistics [Cormen, p187]:
     * find the ith smallest element of the array between indices p and r */
    double randomizedSelect(double[] array, int p, int r, int i, MersenneTwisterFast rng)
        {
        if(p==r) return array[p];
        int q = randomizedPartition(array, p, r, rng);
        int k = q-p+1;
        if(i<=k)
            return randomizedSelect(array, p, q, i,rng);
        else
            return randomizedSelect(array, q+1, r, i-k,rng);
        }


    /* [Cormen, p162] */
    int randomizedPartition(double[] array, int p, int r, MersenneTwisterFast rng)
        {
        int i = rng.nextInt(r-p+1)+p;

        //exchange array[p]<->array[i]
        double tmp = array[i];
        array[i]=array[p];
        array[p]=tmp;
        return partition(array,p,r);
        }


    /* [cormen p 154] */
    int partition(double[] array, int p, int r)
        {
        double x = array[p];
        int i = p-1;
        int j = r+1;
        while(true)
            {
            do j--; while(array[j]>x);
            do i++; while(array[i]<x);
            if ( i < j )
                {
                //exchange array[i]<->array[j]
                double tmp = array[i];
                array[i]=array[j];
                array[j]=tmp;
                }
            else
                return j;
            }
        }
    }