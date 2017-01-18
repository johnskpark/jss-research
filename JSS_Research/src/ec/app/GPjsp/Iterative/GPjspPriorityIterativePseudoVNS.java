/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.GPjsp.Iterative;
import SmallStatistics.SmallStatistics;
import ec.util.*;
import ec.*;
import ec.app.GPjsp.JSPData;
import ec.gp.*;
import ec.gp.koza.*;
import ec.simple.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import jsp.Job;
import jsp.JSPFramework;
import jsp.Machine;

public class GPjspPriorityIterativePseudoVNS extends GPProblem implements SimpleProblemForm {
    public static String fitness = "";
    public static String objective = "";
    
    public JSPData input;

    public JSPFramework[] jspTrainning = new JSPFramework[105];

    public Object clone(){
        GPjspPriorityIterativePseudoVNS newobj = (GPjspPriorityIterativePseudoVNS) (super.clone());
        newobj.input = (JSPData)(input.clone());
        return newobj;
    }

    public void setup(final EvolutionState state,final Parameter base) {
        // very important, remember this
        super.setup(state,base);
                for (int i = 0; i < jspTrainning.length; i++) {
                    jspTrainning[i] = new JSPFramework();
                try {
                    jspTrainning[i].getJSPdata(i*2 + 1);
                    //jspTrainning[i].getJSPdata(96);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(GPjspPriorityIterativePseudoVNS.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(GPjspPriorityIterativePseudoVNS.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        // set up our input -- don't want to use the default base, it's unsafe here
        input = (JSPData) state.parameters.getInstanceForParameterEq(
            base.push(P_DATA), null, JSPData.class);
        input.setup(state,base.push(P_DATA));
    }

    public void evaluate(final EvolutionState state, 
        final Individual ind, 
        final int subpopulation,
        final int threadnum)
    {
        if (!ind.evaluated)  // don't bother reevaluating
            {
            double dummyNumber = 0;
            int hits = 0;
            SmallStatistics result = new SmallStatistics();
            //*
            for (int instance = 0; instance < jspTrainning.length; instance++){
                double tempObj = Double.POSITIVE_INFINITY;
                boolean[] isApplied_Nk = new boolean[((GPIndividual)ind).trees.length]; //Arrays.fill(isApplied_Nk, Boolean.TRUE);
                int Nk = 0; // index of the Iterative dispatching rule to be used
                jspTrainning[instance].resetALL();
                input.firstIteration = true;
                do {
                    //start evaluate schedule
                    input.abjsp = jspTrainning[instance];
                    jspTrainning[instance].reset();
                    int N = jspTrainning[instance].getNumberofOperations();
                    jspTrainning[instance].initilizeSchedule();
                    int nScheduledOp = 0;
                    //choose the next machine to be schedule
                    while (nScheduledOp<N){
                        Machine M = jspTrainning[instance].Machines[jspTrainning[instance].nextMachine()];
                        input.M = M;

                        jspTrainning[instance].setScheduleStrategy(Machine.scheduleStrategy.NONDELAY);
                        // determine priority of jobs in queue
                        jspTrainning[instance].setInitalPriority(M);
                        ((GPIndividual)ind).trees[Nk].child.eval(
                            state,threadnum,input,stack,((GPIndividual)ind),this);
                        for (Job J:M.getQueue()) {
                            J.addPriority(J.tempPriority);
                        }
                        ///////////////////////////////////////
                        jspTrainning[instance].sortJobInQueue(M);
                        Job J = M.completeJob();
                        if (!J.isCompleted()) jspTrainning[instance].Machines[J.getCurrentMachine()].joinQueue(J);
                        nScheduledOp++;
                    }
                    double currentObj = -100;
                    if (objective.equals("Cmax"))
                        currentObj = jspTrainning[instance].getCmax();
                    else if (objective.equals("TWT")) {
                        currentObj = jspTrainning[instance].getTotalWeightedTardiness();
                    }                    
                    if (tempObj > currentObj){
                        tempObj = currentObj;
                        jspTrainning[instance].recordSchedule();
                        Arrays.fill(isApplied_Nk, Boolean.FALSE);
                    }
                    else {
                        isApplied_Nk[Nk] = true;
                        if (!isNextApplied(Nk, isApplied_Nk)) Nk = circleShift(Nk, isApplied_Nk.length);
                        else break;
                    }
                    input.firstIteration = false;
                } while(true);
                if (objective.equals("Cmax"))
                    result.add(jspTrainning[instance].getDevLBCmax(tempObj));
                else if (objective.equals("TWT")) {
                    result.add(jspTrainning[instance].getDevREFTotalWeightedTardiness(tempObj));
                    dummyNumber = 100;
                }
                if (jspTrainning[instance].getDevLBCmax()==0) hits++;
            }

            KozaFitnessOriginal f = ((KozaFitnessOriginal)ind.fitness);
            if (fitness.equals("avg")) f.setStandardizedFitness(state,(float)(dummyNumber+result.getAverage()));
            else if (fitness.equals("max")) f.setStandardizedFitness(state,(float)(dummyNumber+result.getMax()));
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
                JSPFramework[] jspTesting = new JSPFramework[nInstances];
                for (int i = 0; i < jspTesting.length; i++) {
                    jspTesting[i] = new JSPFramework();
                    try {
                        jspTesting[i].getJSPdata(i*2 + startIndex);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(GPjspPriorityIterativePseudoVNS.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(GPjspPriorityIterativePseudoVNS.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                jspTrainning = jspTesting;
                int hits = 0;
                SmallStatistics result = new SmallStatistics();
                for (int instance = 0; instance < jspTrainning.length; instance++){
                    double tempObj = Double.POSITIVE_INFINITY;
                    boolean[] isApplied_Nk = new boolean[((GPIndividual)best_i).trees.length]; //Arrays.fill(isApplied_Nk, Boolean.TRUE);
                    int Nk = 0; // index of the Iterative dispatching rule to be used
                    jspTrainning[instance].resetALL();
                    input.firstIteration = true;
                    do {
                        //start evaluate schedule
                        input.abjsp = jspTrainning[instance];
                        jspTrainning[instance].reset();
                        int N = jspTrainning[instance].getNumberofOperations();
                        jspTrainning[instance].initilizeSchedule();
                        int nScheduledOp = 0;
                        //choose the next machine to be schedule
                        while (nScheduledOp<N){
                            Machine M = jspTrainning[instance].Machines[jspTrainning[instance].nextMachine()];
                            input.M = M;

                            jspTrainning[instance].setScheduleStrategy(Machine.scheduleStrategy.NONDELAY);
                            // determine priority of jobs in queue
                            jspTrainning[instance].setInitalPriority(M);
                            ((GPIndividual)best_i).trees[Nk].child.eval(
                                state,threadnum,input,stack,((GPIndividual)best_i),this);
                            for (Job J:M.getQueue()) {
                                J.addPriority(J.tempPriority);
                            }
                            ///////////////////////////////////////
                            jspTrainning[instance].sortJobInQueue(M);
                            Job J = M.completeJob();
                            if (!J.isCompleted()) jspTrainning[instance].Machines[J.getCurrentMachine()].joinQueue(J);
                            nScheduledOp++;
                        }
                        double currentObj = -100;
                        if (objective.equals("Cmax"))
                            currentObj = jspTrainning[instance].getCmax();
                        else if (objective.equals("TWT")) {
                            currentObj = jspTrainning[instance].getTotalWeightedTardiness();
                        }
                        if (tempObj > currentObj){
                            tempObj = currentObj;
                            jspTrainning[instance].recordSchedule();
                            Arrays.fill(isApplied_Nk, Boolean.FALSE);
                        }
                        else {
                            isApplied_Nk[Nk] = true;
                            if (!isNextApplied(Nk, isApplied_Nk)) Nk = circleShift(Nk, isApplied_Nk.length);
                            else break;
                        }
                        input.firstIteration = false;
                    } while(true);
                    if (objective.equals("Cmax"))
                        result.add(jspTrainning[instance].getDevLBCmax(tempObj));
                    else if (objective.equals("TWT")) {
                        result.add(jspTrainning[instance].getDevREFTotalWeightedTardiness(tempObj));
                    }
                    if (jspTrainning[instance].getDevLBCmax()==0) hits++;
                }
                return "Performance on test set: [Average DEV = " + result.getAverage() +
                        ", Min DEV = " + result.getMin() + ", Max DEV = " +
                        result.getMax() + ", Optimal hits = " + hits + "]";
        }
        public int circleShift(int current, int total){
            if (current+1==total) return 0;
            else return current+1;
        }
        public boolean isNextApplied(int current, boolean[] set){
            if (current+1==set.length) return set[0];
            else return set[current+1];
        }
}

