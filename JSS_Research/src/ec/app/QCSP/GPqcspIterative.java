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
import java.util.logging.Level;
import java.util.logging.Logger;


public class GPqcspIterative extends GPProblem implements SimpleProblemForm {

    QCSP[] train;
    //double[] lb = {520,508,513,510,515,513,511,513,512,549};
    //double[] lb = {506,506,505,507,506,507,507,506,506,507};
    double[] lb = {1014,1104,1107,1202,1015,1136,1098,1151,1023,1015};
    public qcspData input;

    public Object clone(){
        GPqcspIterative newobj = (GPqcspIterative) (super.clone());
        newobj.input = (qcspData)(input.clone());
        return newobj;
    }

    public void setup(final EvolutionState state,final Parameter base) {
        // very important, remember this
        super.setup(state,base);
            //input training data;

        train = new QCSP[10];
        for (int i = 0; i < train.length; i++) {
            DecimalFormat df2 = new DecimalFormat( "000" );
            try {
                train[i] = new QCSP("/benchmark/QCSP_n100_b20_c600_f50_uni_d100_g0_q6_t1_s1_" + df2.format(i + 1) + ".txt");
            } catch (IOException ex) {
                Logger.getLogger(GPqcspIterative.class.getName()).log(Level.SEVERE, null, ex);
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
                train[i].setupGPIndividual((GPIndividual) ind,input, threadnum, state, stack, this);
                double obj = train[i].iterativeConstructSchedule(5);
                result.add((obj-lb[i])/lb[i]);
                //result.add((obj-train[i].lb)/train[i].lb);
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
                //f.results+= "\n" + getTestPerformance(state, threadnum, best_i, 2, 105);   //la
            }
        }
        private String getTestPerformance(final EvolutionState state, final int threadnum, Individual best_i, int startIndex, int nInstances){
            SmallStatistics result = new SmallStatistics();
            String summary ="\n";
                return "Performance on test set: [Average DEV = " + result.getAverage() +
                        ", Min DEV = " + result.getMin() + ", Max DEV = " +
                        result.getMax() + ", Optimal hits = "  + "]" + summary;
        }
}

