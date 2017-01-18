/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.GPjsp;
import SmallStatistics.SmallStatistics;
import ec.util.*;
import ec.*;
import ec.gp.*;
import ec.multiobjective.MultiObjectiveFitness;
import ec.multiobjective.MultiObjectiveStatistics;
import ec.simple.*;
import jsp.DynamicJSPFramework;
import jsp.Job;
import jsp.Machine;

public class GPjspMixTestMODynamic extends GPProblem implements SimpleProblemForm {
    public static String fitness = "";
    public static String objective = "";
    public jspData input;
    public static int[] SimSeed = {2734,72734,	72605,12628,20029,1991,
                        55013,84005,54972,80531,45414,25675,
                        79032,14882,17423,2798,77874,3805,
                        21671,51204,85187,76476,12363,92832,
                        36503,25237,26178,13614,50288,26279};
    public static double[] utilisation = {0.7,0.8,0.9};
    public Object clone(){
        GPjspMixTestMODynamic newobj = (GPjspMixTestMODynamic) (super.clone());
        newobj.input = (jspData)(input.clone());
        return newobj;
    }

    public void setup(final EvolutionState state,final Parameter base) {
        // very important, remember this
        super.setup(state,base);

        // set up our input -- don't want to use the default base, it's unsafe here
        input = (jspData) state.parameters.getInstanceForParameterEq(
            base.push(P_DATA), null, jspData.class);
        input.setup(state,base.push(P_DATA));
    }

    public void evaluate(final EvolutionState state,
        final Individual ind,
        final int subpopulation,
        final int threadnum)
    {

        if (!ind.evaluated)  // don't bother reevaluating
            {
            double[] objectives = ((MultiObjectiveFitness) ind.fitness).getObjectives();
            SmallStatistics[] result = new SmallStatistics[2];
            result[0]= new SmallStatistics();
            result[1]= new SmallStatistics();
            //*
            for (int ds = 0; ds < 30; ds++) {
                for (double u : utilisation){
                DynamicJSPFramework jspDynamic = new DynamicJSPFramework(SimSeed[ds],6,u,100,500);
                input.abjsp = jspDynamic;
                //set dispatching rule
                Machine.priorityType PT = Machine.priorityType.CONV;
                jspDynamic.setPriorityType(PT);
                jspDynamic.setScheduleStrategy(Machine.scheduleStrategy.NONDELAY);
                //////////////////////////////////////////////
                jspDynamic.setNextArrivalTime();
                while (!jspDynamic.isStop()) {
                    if (jspDynamic.isNextArrivalEvent()) {
                        jspDynamic.generateRandomJob(jspDynamic.getNextArrivalTime());
                        jspDynamic.setNextArrivalTime();
                    } else {
                        jspDynamic.unplanAll();
                        do {
                            int nextMachine = jspDynamic.nextMachine();
                            if (nextMachine<0)
                                break;
                            Machine M = jspDynamic.machines[nextMachine];
                            input.M = M;
                            jspDynamic.setInitalPriority(M);
                            // determine priority of jobs in queue
                            if (M.getQueue().size()>1){
                                ((GPIndividual)ind).trees[0].child.eval(
                                    state,threadnum,input,stack,((GPIndividual)ind),this);
                                for (Job J:M.getQueue()) {
                                   J.addPriority(J.tempPriority);
                                }
                                jspDynamic.sortJobInQueue(M);
                            }
                            if (M.getPlannedStartTimeNextOperation()<=jspDynamic.getNextArrivalTime()){
                                Job J = M.completeJob();
                                if (!J.isCompleted()) jspDynamic.machines[J.getCurrentMachine()].joinQueue(J);
                                else jspDynamic.removeJobFromSystem(J);
                            } else
                                M.plan();
                        } while(true);
                    }
                }
                result[0].add(jspDynamic.getCmax());
                result[1].add(jspDynamic.getNormalisedTotalWeightedTardiness());
                }
            }

            objectives[0] = (float)(result[0].getAverage());
            objectives[1] = (float)(result[1].getAverage());
            ((MultiObjectiveFitness)ind.fitness).setObjectives(state, objectives);
            //System.out.print("|");
            ind.evaluated = true;
            }
     }
        public void finishEvaluating(final EvolutionState state, final int threadnum)
        {
            //System.out.println("");
            if (state.generation == 50) {
                MultiObjectiveStatistics myMOStat = (MultiObjectiveStatistics) state.statistics;
                //myMOStat.myFinalStatistic(state, threadnum,this,threadnum, 2, 105);
            }
        }
        public String getTestPerformance(final EvolutionState state, final int threadnum, Individual ind, int startIndex, int nInstances){
            SmallStatistics[] result = new SmallStatistics[2];
            result[0]= new SmallStatistics();
            result[1]= new SmallStatistics();
            //*
            for (int ds = 0; ds < 30; ds++) {
                for (double u : utilisation){
                DynamicJSPFramework jspDynamic = new DynamicJSPFramework(SimSeed[ds],10,u,100,500);
                input.abjsp = jspDynamic;
                //set dispatching rule
                Machine.priorityType PT = Machine.priorityType.CONV;
                jspDynamic.setPriorityType(PT);
                jspDynamic.setScheduleStrategy(Machine.scheduleStrategy.NONDELAY);
                //////////////////////////////////////////////
                jspDynamic.setNextArrivalTime();
                while (!jspDynamic.isStop()) {
                    if (jspDynamic.isNextArrivalEvent()) {
                        jspDynamic.generateRandomJob(jspDynamic.getNextArrivalTime());
                        jspDynamic.setNextArrivalTime();
                    } else {
                        jspDynamic.unplanAll();
                        do {
                            int nextMachine = jspDynamic.nextMachine();
                            if (nextMachine<0)
                                break;
                            Machine M = jspDynamic.machines[nextMachine];
                            input.M = M;
                            jspDynamic.setInitalPriority(M);
                            // determine priority of jobs in queue
                            if (M.getQueue().size()>1){
                                ((GPIndividual)ind).trees[0].child.eval(
                                    state,threadnum,input,stack,((GPIndividual)ind),this);
                                for (Job J:M.getQueue()) {
                                   J.addPriority(J.tempPriority);
                                }
                                jspDynamic.sortJobInQueue(M);
                            }
                            if (M.getPlannedStartTimeNextOperation()<=jspDynamic.getNextArrivalTime()){
                                Job J = M.completeJob();
                                if (!J.isCompleted()) jspDynamic.machines[J.getCurrentMachine()].joinQueue(J);
                                else jspDynamic.removeJobFromSystem(J);
                            } else
                                M.plan();
                        } while(true);
                    }
                }
                result[0].add(jspDynamic.getCmax());
                result[1].add(jspDynamic.getNormalisedTotalWeightedTardiness());
                }
            }
            return result[0].getAverage() + " " + result[1].getAverage();
        }
}

