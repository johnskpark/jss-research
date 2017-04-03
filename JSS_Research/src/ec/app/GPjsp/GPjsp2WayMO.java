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
import ec.multiobjective.MultiObjectiveStatisticsSu;
import ec.simple.*;
import jsp.DynamicJSPFramework;
import jsp.Job;
import jsp.Machine;

public class GPjsp2WayMO extends GPProblem implements SimpleProblemForm {
    public double meanTime = 1;
    public static String fitness = "";
    public static String objective = "";
    public JSPData input;
    public static int[] SimSeed = {2734,72734,	72605,12628,20029,1991,
                        55013,84005,54972,80531,45414,25675,
                        79032,14882,17423,2798,77874,3805,
                        21671,51204,85187,76476,12363,92832,
                        36503,25237,26178,13614,50288,26279};
    public Object clone(){
        GPjsp2WayMO newobj = (GPjsp2WayMO) (super.clone());
        newobj.input = (JSPData)(input.clone());
        return newobj;
    }

    public void setup(final EvolutionState state,final Parameter base) {
        // very important, remember this
        super.setup(state,base);

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
            double[] utilisation = {0.8,0.9};
            int[] numbeOfMachines = {4,6};
            String[] lowers = {"miss"};
            String[] dists = {"expo"};
            double[] objectives = ((MultiObjectiveFitness)ind.fitness).getObjectives();
            SmallStatistics[] result = new SmallStatistics[2];
            result[0]= new SmallStatistics();
            result[1]= new SmallStatistics();
            SmallStatistics resultDD = new SmallStatistics();
            outerLoop:
            for (String dist : dists){for (String s : lowers){ for (int m : numbeOfMachines){ for (double u : utilisation){
                for (int ds = 0; ds < 1; ds++) {
                    int lower = 0;
                    String distribution ="";
                    double param = -1;
                    if ("miss".equals(s)) lower = 1; else lower = m;
                    if ("expo".equals(dist)){
                        distribution = "erlang";
                        param = 1;
                    } else if ("erlang2".equals(dist)) {
                        distribution = "erlang";
                        param = 2;
                    } else if ("uniform".equals(dist)) {
                        distribution = "uniform";
                        param = 0.5;
                    }
                    DynamicJSPFramework jspDynamic = new DynamicJSPFramework(SimSeed[ds],m,lower,m,u,u,meanTime,distribution,param,1000,5000);
                    input.abJSP = jspDynamic;
                    //set dispatching rule
                    Machine.priorityType PT = Machine.priorityType.CONV;
                    jspDynamic.setPriorityType(PT);
                    jspDynamic.setScheduleStrategy(Machine.scheduleStrategy.NONDELAY);
                    //////////////////////////////////////////////
                    jspDynamic.setNextArrivalTime();
                    while (!jspDynamic.isStop()) {
                        if (jspDynamic.isNextArrivalEvent()) {
                            //JOB newjob = jspDynamic.GenerateNonRecirculatedJob(jspDynamic.getNextArrivalTime());
                            ///*
                            Job newjob = jspDynamic.generateRandomJob(jspDynamic.getNextArrivalTime());
                            input.partialEstimatedFlowtime = 0;
                            input.job = newjob;
                            for (int i = 0; i < newjob.getNumberOperations(); i++) {
                                input.stat.gatherStatFromJSPModel(jspDynamic, m , newjob,i , input.partialEstimatedFlowtime);
                                //calculcate parital flowtime
                                input.tempVal = 0;
                                input.k = i;
                                ((GPIndividual)ind).trees[1].child.eval(
                                    state,threadnum,input,stack,((GPIndividual)ind),this);
                                input.partialEstimatedFlowtime += input.tempVal;
                            }
                            if (input.partialEstimatedFlowtime < 0.0 || input.partialEstimatedFlowtime
                                    == Double.POSITIVE_INFINITY || Double.isNaN(input.partialEstimatedFlowtime)){
                                resultDD = new SmallStatistics();
                                break outerLoop;
                            }
                            newjob.assignDuedate(input.partialEstimatedFlowtime);
                            jspDynamic.setNextArrivalTime();
                             //*/
                            //newjob.assignDuedate(1.3*newjob.getTotalProcessingTime());
                            //jspDynamic.setNextArrivalTime();
                        } else {
                            jspDynamic.unplanAll();
                            do {
                                int nextMachine = jspDynamic.nextMachine();
                                if (nextMachine<0)
                                    break;
                                Machine M = jspDynamic.machines[nextMachine];
                                input.machine = M;
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
                    double mpea = jspDynamic.getMAPE();
                    if (mpea < 0.0 || mpea == Double.POSITIVE_INFINITY || Double.isNaN(mpea))
                        break outerLoop;
                    resultDD.add(mpea);
                    result[0].add(jspDynamic.getCmax());
                    result[1].add(jspDynamic.getNormalisedTotalWeightedTardiness());
                    jspDynamic.printMachinesUtilisation();
                }
            }}}}

            objectives[0] = result[0].getAverage();
            objectives[1] = result[1].getAverage();
            objectives[2] = resultDD.getAverage();

            for (int i = 0; i < 3; i++) {
                if (objectives[i]  < 0.0f || objectives[i] == Float.POSITIVE_INFINITY || Double.isNaN(objectives[i]))
                {
                    objectives[i] = Float.POSITIVE_INFINITY;
                }
                if (i==2&&objectives[2]>3){
                    for (int j = 0; j < 3; j++) objectives[j]=1000000;
                    break;
                }
            }
            ((MultiObjectiveFitness)ind.fitness).setObjectives(false, objectives);
            ind.evaluated = true;
            System.out.print("|");// + ((GPIndividual)ind).trees[0].child.numNodes(GPNode.NODESEARCH_ALL) + "**" + ((GPIndividual)ind).trees[1].child.numNodes(GPNode.NODESEARCH_ALL));
            }
     }
        public void finishEvaluating(final EvolutionState state, final int threadnum)
        {
            System.out.println("*");
            if (state.generation == state.numGenerations-1) {
                MultiObjectiveStatisticsSu myMOStat = (MultiObjectiveStatisticsSu) state.statistics;
                myMOStat.myFinalStatistic(state, threadnum,this,threadnum, 2, 105);
                //FinalStatisticMO2Way.myFinalStatistic(state, threadnum,this,threadnum, 2, 105);
            }
        }
        public String getTestPerformance(final EvolutionState state, final int threadnum, Individual ind, int startIndex, int nInstances){
            //double[] utilisation = {0.7,0.8,0.9,0.95};
            //int[] numbeOfMachines = {5,10,20};
            //String[] lowers = {"miss","full"};
            //String[] dists = {"expo","uniform"};
            double[] utilisation = {0.9};
            int[] numbeOfMachines = {5};
            String[] lowers = {"full"};
            String[] dists = {"expo"};
            SmallStatistics[] result = new SmallStatistics[2];
            SmallStatistics resultDD = new SmallStatistics();
            result[0]= new SmallStatistics();
            result[1]= new SmallStatistics();
            String detail = "";
            outerLoop:
            for (String dist : dists){for (String s : lowers){ for (int m : numbeOfMachines){ for (double u : utilisation){

                for (int ds = 0; ds < 30; ds++) {
                    int lower = 0;
                    String distribution ="";
                    double param = -1;
                    if ("miss".equals(s)) lower = 1; else lower = m;
                    if ("expo".equals(dist)){
                        distribution = "erlang";
                        param = 1;
                    } else if ("erlang2".equals(dist)) {
                        distribution = "erlang";
                        param = 2;
                    } else if ("uniform".equals(dist)) {
                        distribution = "uniform";
                        param = 0.5;
                    }
                    DynamicJSPFramework jspDynamic = new DynamicJSPFramework(SimSeed[ds],m,lower,m,u,u,meanTime,distribution,param,1000,5000);
                    input.abJSP = jspDynamic;
                    //set dispatching rule
                    Machine.priorityType PT = Machine.priorityType.CONV;
                    jspDynamic.setPriorityType(PT);
                    jspDynamic.setScheduleStrategy(Machine.scheduleStrategy.NONDELAY);
                    //////////////////////////////////////////////
                    jspDynamic.setNextArrivalTime();
                    while (!jspDynamic.isStop()) {
                        if (jspDynamic.isNextArrivalEvent()) {
                            Job newjob = jspDynamic.generateRandomJob(jspDynamic.getNextArrivalTime());
                            input.partialEstimatedFlowtime = 0;
                            input.job = newjob;
                            for (int i = 0; i < newjob.getNumberOperations(); i++) {
                                input.stat.gatherStatFromJSPModel(jspDynamic, m , newjob,i , input.partialEstimatedFlowtime);
                                //calculcate parital flowtime
                                input.tempVal = 0;
                                input.k = i;
                                ((GPIndividual)ind).trees[1].child.eval(
                                    state,threadnum,input,stack,((GPIndividual)ind),this);
                                input.partialEstimatedFlowtime += input.tempVal;
                            }
                            if (input.partialEstimatedFlowtime < 0.0 || input.partialEstimatedFlowtime
                                    == Double.POSITIVE_INFINITY || Double.isNaN(input.partialEstimatedFlowtime)){
                                resultDD = new SmallStatistics();
                                break outerLoop;
                            }
                            newjob.assignDuedate(input.partialEstimatedFlowtime);
                            jspDynamic.setNextArrivalTime();
                        } else {
                            jspDynamic.unplanAll();
                            do {
                                int nextMachine = jspDynamic.nextMachine();
                                if (nextMachine<0)
                                    break;
                                Machine M = jspDynamic.machines[nextMachine];
                                input.machine = M;
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
                    double mpea = jspDynamic.getMAPE();
                    if (mpea < 0.0 || mpea == Double.POSITIVE_INFINITY || Double.isNaN(mpea))
                        break outerLoop;
                    resultDD.add(mpea);
                    result[0].add(jspDynamic.getCmax());
                    result[1].add(jspDynamic.getNormalisedTotalWeightedTardiness());
                    detail += jspDynamic.getCmax() + " " + jspDynamic.getNormalisedTotalWeightedTardiness() + " " + mpea + " ";
                    }
                }}}}
            return detail + "\n" + result[0].getAverage() + " " + result[1].getAverage() + " " + resultDD.getAverage();
            //return result[0].getAverage() + " " + result[1].getAverage() + " " + resultDD.getAverage();
        }
}

/*
            double[] utilisation = {0.6,0.7,0.8,0.9,0.95};
            int[] numbeOfMachines = {4,5,6,10,20};
            String[] lowers = {"miss","full"};
            String[] dists = {"expo","erlang2","uniform"};
 */