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


public class GPqcsp extends GPProblem implements SimpleProblemForm {
    QCSP[][] train;
    public static int from = 0;
    public static int fromInstance = 5;
    public static long runningTime = -1;
    public static long findTime;
    public static long timeLimit = 100; //second
    public static String name;
    public static double best;
    public static boolean local;
    public static int maxStep = 50;
    public static int indIndex = 0;
    public static QCSP INS;
    public qcspData input;
    public Random rnd = new Random(99999);
    public Object clone(){
        GPqcsp newobj = (GPqcsp) (super.clone());
        newobj.input = (qcspData)(input.clone());
        return newobj;
    }

    public void setup(final EvolutionState state,final Parameter base) {
        // very important, remember this
        super.setup(state,base);
            //input training data;
        best = Integer.MAX_VALUE;
        train = new QCSP[1][1];
        for (int i = 0; i < train.length; i++) {
            for (int j = 0; j < train[i].length; j++) {
                DecimalFormat df2 = new DecimalFormat( "000" );
                try {
                    System.out.println(QCSP.dataset[i+from] + df2.format(j + fromInstance + 1));
                    train[i][j] = new QCSP(QCSP.dataset[i+from] + df2.format(j + fromInstance + 1) + ".txt");
                    train[i][j].resetBestPenalty();
                    name = QCSP.dataset[i+from] + df2.format(j + fromInstance + 1)  ;
                } catch (IOException ex) {
                    Logger.getLogger(GPqcsp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        INS = train[0][0];
        // set up our input -- don't want to use the default base, it's unsafe here
        input = (qcspData) state.parameters.getInstanceForParameterEq(
            base.push(P_DATA), null, qcspData.class);
        input.setup(state,base.push(P_DATA));
        runningTime = System.currentTimeMillis();
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
                    if (indIndex >= state.population.subpops[0].individuals.length / 2) train[i][j].unidirection = false;
                    else train[i][j].unidirection = true;
                    train[i][j].setupGPIndividual((GPIndividual) ind,input, threadnum, state, stack, this);
                    train[i][j].LOCAL_MODE = false;
                    double obj = train[i][j].constructSchedule();
                    if (local){
                        double[] sol = train[i][j].getOrder(train[i][j].sequence);
                        obj = train[i][j].LS1(maxStep, (int)obj,sol);
                    }
                    result.add(obj);
                    if (best>obj) {
                        best = obj;
                        findTime = (System.currentTimeMillis()-runningTime)/1000;
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
            indIndex++;
            ind.evaluated = true;
            }
     }
        public void finishEvaluating(final EvolutionState state, final int threadnum)
        {
            if (!local&&QCSP.uncertainty) INS.localsearchBest(best, maxStep);
            indIndex = 0;
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
                getTestPerformance(state, threadnum, best_i, 2, 105); 
            }
        }
        private String getTestPerformance(final EvolutionState state, final int threadnum, Individual best_i, int startIndex, int nInstances){
            runningTime = (System.currentTimeMillis()-runningTime)/1000;
            return "";
        }
}

