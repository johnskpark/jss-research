/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.OAS;
import SmallStatistics.SmallStatistics;
import ec.util.*;
import ec.*;
import ec.app.QCSP.Core.IndexMaxPQ;
import ec.gp.*;
import ec.gp.koza.*;
import ec.simple.*;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GPoas extends GPProblem implements SimpleProblemForm {
    int[] NN = {100}; int[] TTao = {5}; int[] RR = {5}; int[] INS = {1};
    public oasData input;
    OASUniform[][][][] train; OAS[][][][] traintrain;
    public Random rnd = new Random(99999);
    public Object clone(){
        GPoas newobj = (GPoas) (super.clone());
        newobj.input = (oasData)(input.clone());
        return newobj;
    }

    public void setup(final EvolutionState state,final Parameter base) {
        // very important, remember this
        super.setup(state,base);
            //input training data;
        train = new OASUniform[NN.length][TTao.length][RR.length][INS.length];
        traintrain = new OAS[NN.length][TTao.length][RR.length][INS.length];
        for (int i = 0; i < train.length; i++) {
            for (int j = 0; j < train[i].length; j++) {
                for (int k = 0; k < train[i][j].length; k++) {
                    for (int l = 0; l < train[i][j][k].length; l++) {
                        try {
                            train[i][j][k][l] = new OASUniform(1, NN[i], TTao[j], RR[k], INS[l]);
                            traintrain[i][j][k][l] = new OAS(1, NN[i], TTao[j], RR[k], INS[l]);
                        } catch (IOException ex) {
                            Logger.getLogger(GPoas.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
        try {
            train[0][0][0][0].getRefSolution();
        } catch (IOException ex) {
            Logger.getLogger(GPoas.class.getName()).log(Level.SEVERE, null, ex);
        }
        // set up our input -- don't want to use the default base, it's unsafe here
        input = (oasData) state.parameters.getInstanceForParameterEq(
            base.push(P_DATA), null, oasData.class);
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
            for (int i = 0; i < train.length; i++) {
                for (int j = 0; j < train[i].length; j++) {
                    for (int k = 0; k < train[i][j].length; k++) {
                        for (int l = 0; l < train[i][j][k].length; l++) {
                            train[i][j][k][l].setupGPIndividual((GPIndividual) ind,input, threadnum, state, stack, this);
                            double bestALL = Double.NEGATIVE_INFINITY;
                            for (int m = 0; m < ((GPIndividual) ind).trees.length; m++) {
                                //double obj = train[i][j][k][l].generateActiveGPSchedule(m);
                                double obj = 1;
                                if (bestALL < obj) bestALL = obj;
                            }
                            IndexMaxPQ<Double> x = new IndexMaxPQ(train[i][j][k][l].n);
                            for (int m = 0; m < train[i][j][k][l].priority.length; m++) {
                                x.insert(m, train[i][j][k][l].priority[m]);
                            }
                            //
                            double ref = OAS.getUB(NN[i],TTao[j],RR[k],INS[l]);
                            //result.add((ref - bestALL)/ref);
                            result.add(ref - bestALL);
                        }
                    }
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
                f.results+= "\n" + getTestPerformance(state, threadnum, best_i);   //la
            } else if (state.generation > 1) {
                SmallStatistics result = new SmallStatistics();
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
                for (int i = 0; i < train.length; i++) {
                    for (int j = 0; j < train[i].length; j++) {
                        for (int k = 0; k < train[i][j].length; k++) {
                            for (int l = 0; l < train[i][j][k].length; l++) {
                                train[i][j][k][l].setupGPIndividual((GPIndividual) best_i,input, threadnum, state, stack, this);
                                double bestALL = Double.NEGATIVE_INFINITY;
                                for (int m = 0; m < ((GPIndividual) best_i).trees.length; m++) {
                                    double obj = 1;// train[i][j][k][l].generateActiveGPSchedule(m);
                                    if (bestALL < obj) bestALL = obj;
                                }
                                IndexMaxPQ<Double> x = new IndexMaxPQ(train[i][j][k][l].n);
                                for (int m = 0; m < train[i][j][k][l].priority.length; m++) {
                                    x.insert(m, train[i][j][k][l].priority[m]);
                                }
                                OASsol sol = new OASsol();
                                sol.order = new int[train[i][j][k][l].n];
                                sol.acc = new boolean[train[i][j][k][l].n];
                                int count = 0;
                                for (int m: x) {
                                    sol.order[count] = m;
                                    count++;
                                }
                                for (int m = 0; m < train[i][j][k][l].priority.length; m++) {
                                    if (train[i][j][k][l].priority[m] >= 0.5) sol.acc[m] = true;
                                }
                                sol.obj = bestALL;
                                sol.n = train[i][j][k][l].n;
                                //System.out.println(bestALL);
                                bestALL = traintrain[i][j][k][l].ILS(sol);
                                //
                                double ref = OAS.getUB(NN[i],TTao[j],RR[k],INS[l]);
                                //result.add((ref - bestALL)/ref);
                                result.add(ref - bestALL);
                            }
                        }
                    }
                }
                KozaFitnessOriginal f = ((KozaFitnessOriginal)best_i.fitness);
                f.setStandardizedFitness(state, (float)result.getAverage());
            }

        }
       private String getTestPerformance(final EvolutionState state, final int threadnum, Individual best_i){
                int[] tNN = {100}; int[] tTTao = {5}; int[] tRR = {5}; int[] tINS = {1,2,3,4,5,6,7,8,9,10};
                OASUniform[][][][] test = new OASUniform[tNN.length][tTTao.length][tRR.length][tINS.length];
                OAS[][][][] testtest = new OAS[tNN.length][tTTao.length][tRR.length][tINS.length];
                int hits = 0;
                SmallStatistics result = new SmallStatistics();
                for (int i = 0; i < test.length; i++) {
                    for (int j = 0; j < test[i].length; j++) {
                        for (int k = 0; k < test[i][j].length; k++) {
                            for (int l = 0; l < test[i][j][k].length; l++) {
                                try {
                                    test[i][j][k][l] = new OASUniform(1, tNN[i], tTTao[j], tRR[k], tINS[l]);
                                    testtest[i][j][k][l] = new OAS(1, tNN[i], tTTao[j], tRR[k], tINS[l]);
                                } catch (IOException ex) {
                                    Logger.getLogger(GPoas.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                test[i][j][k][l].setupGPIndividual((GPIndividual) best_i,input, threadnum, state, stack, this);
                                double bestALL = Double.NEGATIVE_INFINITY;
                                for (int m = 0; m < ((GPIndividual) best_i).trees.length; m++) {
                                    double obj = 1;//test[i][j][k][l].generateActiveGPSchedule(m);
                                    if (bestALL < obj) bestALL = obj;
                                }
                                IndexMaxPQ<Double> x = new IndexMaxPQ(test[i][j][k][l].n);
                                for (int m = 0; m < test[i][j][k][l].priority.length; m++) {
                                    x.insert(m, test[i][j][k][l].priority[m]);
                                }
                                OASsol sol = new OASsol();
                                sol.order = new int[test[i][j][k][l].n];
                                sol.acc = new boolean[test[i][j][k][l].n];
                                int count = 0;
                                for (int m: x) {
                                    sol.order[count] = m;
                                    count++;
                                }
                                for (int m = 0; m < test[i][j][k][l].priority.length; m++) {
                                    if (test[i][j][k][l].priority[m] >= 0.5) sol.acc[m] = true;
                                }
                                sol.obj = bestALL;
                                sol.n = test[i][j][k][l].n;
                                System.out.println(bestALL);
                                testtest[i][j][k][l].ILS(sol);
                                double ref = OAS.getUB(tNN[i],tTTao[j],tRR[k],tINS[l]);
                                result.add((ref - bestALL)/ref);
                            }
                        }
                    }
                }

                return "Performance on test set: [Average DEV = " + result.getAverage() +
                        ", Min DEV = " + result.getMin() + ", Max DEV = " +
                        result.getMax() + ", Optimal hits = " + hits + "]";
        }
}

