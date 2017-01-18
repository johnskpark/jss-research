/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.TwoDStripPacking;
import SmallStatistics.SmallStatistics;
import ec.util.*;
import ec.*;
import ec.app.TwoDStripPacking.BestFit.BestFitEfficientLocalSearch;
import ec.gp.*;
import ec.gp.koza.*;
import ec.simple.*;


public class GPtwoDstripLocalSearch extends GPProblem implements SimpleProblemForm {
    int[] index = {10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,13,3,3,3,3,3,3,3,6,6,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10};
    double[] optimum = {40,50,50,80,100,100,100,80,40,50,50,80,100,100,100,80,0,20,15,30,60,90,120,240,100,100};
    String[] nameSet = {"eN1-","eN2-","eN3-","eN4-","eN5-","eN6-","eN7-","eN8-","tN1-","tN2-","tN3-","tN4-","tN5-","tN6-","tN7-","tN8-","N","c1p","c2p","c3p","c4p","c5p","c6p","c7p","nice","path",
    "c1s1i","c1s2i","c1s3i","c1s4i","c1s5i"
    ,"c2s1i","c2s2i","c2s3i","c2s4i","c2s5i"
    ,"c3s1i","c3s2i","c3s3i","c3s4i","c3s5i"
    ,"c4s1i","c4s2i","c4s3i","c4s4i","c4s5i"
    ,"c5s1i","c5s2i","c5s3i","c5s4i","c5s5i"
    ,"c6s1i","c6s2i","c6s3i","c6s4i","c6s5i"
    ,"c7s1i","c7s2i","c7s3i","c7s4i","c7s5i"
    ,"c8s1i","c8s2i","c8s3i","c8s4i","c8s5i"
    ,"c9s1i","c9s2i","c9s3i","c9s4i","c9s5i"
    ,"c10s1i","c10s2i","c10s3i","c10s4i","c10s5i"};

    public twoDStripData input;
    BestFitEfficientLocalSearch[][] TrainingInstances;
    int[] TrainInstanceSet = {3,4,5};

    public Object clone(){
        GPtwoDstripLocalSearch newobj = (GPtwoDstripLocalSearch) (super.clone());
        newobj.input = (twoDStripData)(input.clone());
        return newobj;
    }

    public void setup(final EvolutionState state,final Parameter base) {
        // very important, remember this
        super.setup(state,base);
            //input training data;

        TrainingInstances = new BestFitEfficientLocalSearch[TrainInstanceSet.length][];
            for (int i = 0; i < TrainInstanceSet.length; i++) {
                TrainingInstances[i] = new BestFitEfficientLocalSearch[index[i]];
                for (int j = 0; j < index[i]; j++) {
                     TrainingInstances[i][j] = new BestFitEfficientLocalSearch(TrainInstanceSet[i], j);
                }
            }

        // set up our input -- don't want to use the default base, it's unsafe here
        input = (twoDStripData) state.parameters.getInstanceForParameterEq(
            base.push(P_DATA), null, twoDStripData.class);
        input.setup(state,base.push(P_DATA));
    }

    public void evaluate(final EvolutionState state, 
        final Individual ind, 
        final int subpopulation,
        final int threadnum)
    {
        if (!ind.evaluated)  // don't bother reevaluating
            {
            int hits = 0;
            SmallStatistics result = new SmallStatistics();
            for (int i = 0; i < TrainInstanceSet.length; i++) {
                for (int j = 0; j < index[i]; j++) {
                     TrainingInstances[i][j].setupGPIndividual((GPIndividual) ind,input, threadnum, state, stack, this);
                     double obj = TrainingInstances[i][j].run(TrainInstanceSet[i], j);
                     result.add(0.000001+(obj-optimum[TrainInstanceSet[i]])/optimum[TrainInstanceSet[i]]);
                }
            }

            // the fitness better be KozaFitness!
            KozaFitnessOriginal f = ((KozaFitnessOriginal)ind.fitness);
            
            f.setStandardizedFitness(state, (float)result.getAverage());

            f.min = result.getMin();
            f.average = result.getAverage();
            f.max = result.getMax();
            f.hits = hits;

            ind.evaluated = true;
            }
     }
        public void finishEvaluating(final EvolutionState state, final int threadnum)
        {
            if (state.generation == state.numGenerations-1) {
                Individual best_i;
                SimpleShortStatistics stats = (SimpleShortStatistics) state.statistics;
                //*
                best_i = stats.getBestSoFar()[0];
                for (int y = 0; y < state.population.subpops[0].individuals.length; y++) {
                    // best individual
                    if (state.population.subpops[0].individuals[y].fitness.betterThan(best_i.fitness)) {
                        best_i = state.population.subpops[0].individuals[y];
                    }
                }
                //*
                KozaFitnessOriginal f = (KozaFitnessOriginal)(best_i.fitness);
                f.results+= "\n" + getTestPerformance(state, threadnum, best_i, 2, 105);   //la
            }
        }
        private String getTestPerformance(final EvolutionState state, final int threadnum, Individual best_i, int startIndex, int nInstances){
            SmallStatistics result = new SmallStatistics();
            String summary ="\n";
            int[] TestInstanceSet = new int[76];
            for (int i = 0; i < TestInstanceSet.length; i++) TestInstanceSet[i] = i;
            //int[] instanceSet = {1,9};
            for (int i = 0; i < TestInstanceSet.length; i++) {
                double average =0;
                for (int j = 0; j < index[i]; j++) {
                     BestFitEfficientLocalSearch problem = new BestFitEfficientLocalSearch(TestInstanceSet[i], j);
                     problem.setupGPIndividual((GPIndividual) best_i,input, threadnum, state, stack, this);
                     double obj = problem.run(TestInstanceSet[i], j);
                     if (i<26) {
                         summary += nameSet[TestInstanceSet[i]] + (1+j) + " & " + obj + " \\hline"+"\n";
                         result.add(obj);
                     } else {
                         average += obj;
                     }
                }
                if (i>=26) {
                    average /= 10;
                    summary += nameSet[TestInstanceSet[i]] + " & " + average + " \\hline"+"\n";
                    result.add(average);
                }
            }
            //System.out.println(summary);
                return "Performance on test set: [Average DEV = " + result.getAverage() +
                        ", Min DEV = " + result.getMin() + ", Max DEV = " +
                        result.getMax() + ", Optimal hits = "  + "]" + summary;
        }
}

