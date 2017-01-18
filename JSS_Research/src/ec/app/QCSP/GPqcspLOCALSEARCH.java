/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.QCSP;
import SmallStatistics.SmallStatistics;
import ec.util.*;
import ec.*;
import ec.app.QCSP.Core.QCSP;
import ec.gp.*;
import ec.gp.koza.*;
import ec.simple.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GPqcspLOCALSEARCH extends GPProblem implements SimpleProblemForm {
    QCSP[][] train;
    int from = 5;
    int fromInstance = 4;
    int maxStep = 50;
    int nTop = 100;
    double best = Integer.MAX_VALUE;
    public qcspData input;
    public Random rnd = new Random(99999);
    public double[][] topSol = new double[nTop][];
    public double[] topGPobj = new double[nTop];

    public Object clone(){
        GPqcspLOCALSEARCH newobj = (GPqcspLOCALSEARCH) (super.clone());
        newobj.input = (qcspData)(input.clone());
        return newobj;
    }

    public void setup(final EvolutionState state,final Parameter base) {
        // very important, remember this
        super.setup(state,base);
            //input training data;

        train = new QCSP[1][1];

        for (int i = 0; i < train.length; i++) {
            for (int j = 0; j < train[i].length; j++) {
                DecimalFormat df2 = new DecimalFormat( "000" );
                try {
                    System.out.println(QCSP.dataset[i+from] + df2.format(j + fromInstance + 1));
                    train[i][j] = new QCSP(QCSP.dataset[i+from] + df2.format(j + fromInstance + 1) + ".txt");
                } catch (IOException ex) {
                    Logger.getLogger(GPqcspLOCALSEARCH.class.getName()).log(Level.SEVERE, null, ex);
                }
                for (int k = 0; k < topSol.length; k++) {
                    topSol[k] = new double[train[i][j].getNumberOfTask()];
                    topGPobj[k] = Double.POSITIVE_INFINITY;
                }
            }
        }

        // set up our input -- don't want to use the default base, it's unsafe here
        input = (qcspData) state.parameters.getInstanceForParameterEq(
            base.push(P_DATA), null, qcspData.class);
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
                    train[i][j].setupGPIndividual((GPIndividual) ind,input, threadnum, state, stack, this);
                    train[i][j].LOCAL_MODE = false;
                    double obj = train[i][j].constructSchedule();
                    double[] sol = train[i][j].getOrder(train[i][j].sequence);
                    result.add(obj);
                    updateTopSol(obj, sol);
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
    void updateTopSol(double obj, double[] sol){        
        for (int i = 0; i < nTop; i++) {
            if (obj<topGPobj[i]) {
                for (int j = nTop-1; j > i; j--) {
                    topGPobj[j] = topGPobj[j-1];
                    System.arraycopy(topSol[j-1], 0, topSol[j], 0, topSol[j].length);
                }
                topGPobj[i] = obj;
                System.arraycopy(sol, 0, topSol[i], 0, sol.length);
                break;  
            } else if (obj==topGPobj[i]) break;
        }
    }
    public void finishEvaluating(final EvolutionState state, final int threadnum)
    {
        for (int i = 0; i < nTop; i++) {
            train[0][0].LOCAL_MODE = true;
            double obj = train[0][0].LS1(2000, (int)topGPobj[i], topSol[i]);
            if (obj<best) best = obj;
        }
        System.out.println("Best makespan is " + best);
        for (int k = 0; k < topSol.length; k++) {
            topSol[k] = new double[train[0][0].getNumberOfTask()];
            topGPobj[k] = Double.POSITIVE_INFINITY;
        }
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
            KozaFitnessOriginal.results+= "\n" + getTestPerformance(state, threadnum, best_i, 2, 105);   //la
        }
    }
    private String getTestPerformance(final EvolutionState state, final int threadnum, Individual best_i, int startIndex, int nInstances){
        return "BEST OBJ = " + best;
    }
}

