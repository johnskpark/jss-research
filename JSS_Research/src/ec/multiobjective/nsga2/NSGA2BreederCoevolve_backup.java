/*
  Copyright 2010 by Sean Luke and George Mason University
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package ec.multiobjective.nsga2;

import ec.*;
import ec.app.GPjsp.Coevolutionary2WayGPIndividual;
import ec.util.*;
import ec.simple.*;
import java.util.ArrayList;

/* 
 * NSGA2Breeder.java
 * 
 * Created: Thu Feb 04 2010
 * By: Faisal Abidi and Sean Luke
 */

/**
 * This SimpleBreeder subclass breeds a set of children from the Population, then
 * joins the original Population with the children in a (mu+mu) fashion.   An NSGA2Breeder
 * may have multiple threads for breeding.
 *
 * <p>NSGA-II has fixed archive size (the population size), and so ignores the 'elites'
 * declaration.  However it will adhere to the 'reevaluate-elites' parameter in SimpleBreeder
 * to determine whether to force fitness reevaluation.

 */

public class NSGA2BreederCoevolve_backup extends SimpleBreeder
    {
    public void setup(final EvolutionState state, final Parameter base)
        {
        super.setup(state, base);
        // make sure SimpleBreeder's elites facility isn't being used
        for (int i = 0; i < elite.length; i++)
            if (elite[i] != 0)
                state.output.warning("Elites may not be used with NSGA2Breeder, and will be ignored.");
        }

    /**
     * Override breedPopulation(). We take the result from the super method in
     * SimpleBreeder and append it to the old population. Hence, after
     * generation 0, every subsequent call to
     * <code>NSGA2Evaluator.evaluatePopulation()</code> will be passed a
     * population of 2x<code>originalPopSize</code> individuals.
     */
    public Population breedPopulation(EvolutionState state)
        {
        Individual[] dummy = new Individual[0];
        Population oldPop = (Population) state.population;
        Population newPop = super.breedPopulation(state);
        Individual[] combinedInds;
        Subpopulation[] subpops = oldPop.subpops;
        Subpopulation oldSubpop;
        Subpopulation newSubpop;
        
        int subpopsLength = subpops.length;
        
        for (int i = 0; i < subpopsLength; i++)
            {
            ArrayList _coevolveSubpop = new ArrayList();
            oldSubpop = oldPop.subpops[i];
            newSubpop = newPop.subpops[i];
            if (i==0){
                for (int j = 0; j < oldPop.subpops[1].individuals.length; j++) {
                    if (oldPop.subpops[1].individuals[j].evaluated)
                        _coevolveSubpop.add((Individual)(swapCloneContext(((Coevolutionary2WayGPIndividual)oldPop.subpops[1].individuals[j]),i)));
                }
            } 
            else {
                for (int j = 0; j < oldPop.subpops[0].individuals.length; j++) {
                    if (oldPop.subpops[0].individuals[j].evaluated)
                        _coevolveSubpop.add((Individual)(swapCloneContext(((Coevolutionary2WayGPIndividual)oldPop.subpops[0].individuals[j]),i)));
                }                
            }
            int countNewEval = 0;
            for (int j = 0; j < oldPop.subpops[i].individuals.length; j++) {
                if (!oldPop.subpops[i].individuals[j].evaluated) countNewEval++;
            }
            
            Individual[] coevolveSubpop = (Individual[])(_coevolveSubpop).toArray(dummy);
            combinedInds = new Individual[coevolveSubpop.length + oldSubpop.individuals.length + newSubpop.individuals.length-countNewEval];
            System.arraycopy(coevolveSubpop, 0, combinedInds, 0,  coevolveSubpop.length);
            System.arraycopy(oldSubpop.individuals, 0, combinedInds,  coevolveSubpop.length , oldSubpop.individuals.length);
            System.arraycopy(newSubpop.individuals, 0, combinedInds, coevolveSubpop.length + oldSubpop.individuals.length,  newSubpop.individuals.length-countNewEval);
            
            newSubpop.individuals = combinedInds;
            }
        return newPop;
        }
        public Coevolutionary2WayGPIndividual swapCloneContext(Coevolutionary2WayGPIndividual ind, int subpop){
            Coevolutionary2WayGPIndividual temp = new Coevolutionary2WayGPIndividual();
            if (ind.context[1] == null){
                if (subpop==1) {
                    temp = (Coevolutionary2WayGPIndividual) ind.clone();
                    return temp;
                }
                temp = (Coevolutionary2WayGPIndividual) ind.context[0].clone();
                temp.context = new Coevolutionary2WayGPIndividual[2];
                temp.context[1] = (Coevolutionary2WayGPIndividual) ind.clone();
            } else {
                if (subpop==0) {
                    temp = (Coevolutionary2WayGPIndividual) ind.clone();
                    return temp;
                }
                temp = (Coevolutionary2WayGPIndividual) ind.context[1].clone();    
                temp.context = new Coevolutionary2WayGPIndividual[2];
                temp.context[0] = (Coevolutionary2WayGPIndividual) ind.clone();            
            }
            temp.fitness = (Fitness) ind.fitness.clone();
            temp.evaluated = true;
            return temp;
        }
    }
