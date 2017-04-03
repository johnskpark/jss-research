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

public class GPjspMOGPHH extends GPProblem implements SimpleProblemForm {
    public double meanTime = 25;
    public static String fitness = "";
    public static String objective = "";
    public JSPData input;
    public static int[] SimSeed = {2734,72734,	72605,12628,20029,1991,
                        55013,84005,54972,80531,45414,25675,
                        79032,14882,17423,2798,77874,3805,
                        21671,51204,85187,76476,12363,92832,
                        36503,25237,26178,13614,50288,26279,
    70226,66382,52542,98151,83655,67162,39059,16816,92994,71343,35203,10876,79203,74695,66278,33801,72288,90659,51540,35071
    };
    public Object clone(){
        GPjspMOGPHH newobj = (GPjspMOGPHH) (super.clone());
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
        System.currentTimeMillis();
    }

    public void evaluate(final EvolutionState state,
        final Individual ind,
        final int subpopulation,
        final int threadnum)
    {
        if (!ind.evaluated)  // don't bother reevaluating
            {
            double[] utilisation = {0.8,0.9};
            double[] allowance = {3,5,7};
            int[] numbeOfMachines = {10};
            String[] lowers = {"miss"};
            String[] dists = {"duniform"};
            double[] objectives = ((MultiObjectiveFitness) ind.fitness).getObjectives();
            SmallStatistics[] result = new SmallStatistics[6];
            for (int i = 0; i < result.length; i++) {
                result[i] = new SmallStatistics();
            }

            outerLoop:
            for (String dist : dists){for (String s : lowers){ for (int m : numbeOfMachines){ for (double u : utilisation){
                for (int ds = 0; ds < 5; ds++) {
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
                    } else if ("duniform".equals(dist)) {
                        distribution = "duniform";
                        param = 1;
                    }
                    DynamicJSPFramework.revisit = true;
                    DynamicJSPFramework jspDynamic = new DynamicJSPFramework(SimSeed[ds],m,2,14,u,u,meanTime,distribution,param,500,2000);
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
                                newjob.getOperations()[i].setWait(correctValue(input.tempVal,input.stat.OT));
                            }
                            newjob.assignDuedate(allowance[(int)(allowance.length*jspDynamic.getRandomNumber())]*newjob.getTotalProcessingTime());
                            newjob.setFinishTime(newjob.getReleaseTime()+input.partialEstimatedFlowtime);
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

                    result[0].add(jspDynamic.getMeanFlowtime());
                    result[1].add(jspDynamic.getCmax());
                    result[2].add(jspDynamic.getPercentTardiness());
                    result[3].add(jspDynamic.getMeantardiness());
                    result[4].add(jspDynamic.getMaxTardiness());
                    result[5].add(jspDynamic.getMAEE());
                }
            }}}}

            objectives[0] = (float)(result[0].getAverage());
            objectives[1] = (float)(result[1].getAverage());
            objectives[2] = (float)(result[2].getAverage());
            objectives[3] = (float)(result[3].getAverage());
            objectives[4] = (float)(result[4].getAverage());
            objectives[5] = (float)(correctValue(result[5].getAverage(),99999));

            ((MultiObjectiveFitness)ind.fitness).setObjectives(state, objectives);
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
            double[] allowance = {4,6,8};
            double[] utilisation = {0.85,0.95};
            int[] numbeOfMachines = {10};
            String[] lowers = {"miss"};
            String[] dists = {"duniform"};
            double[] Mean = {25,50};
            SmallStatistics[] result = new SmallStatistics[6];
            for (int i = 0; i < result.length; i++) {
                result[i] = new SmallStatistics();
            }
            String detail = "";
            outerLoop:
            for (String dist : dists){for (String s : lowers){ for (int m : numbeOfMachines){ for (double mean : Mean){ for (double u : utilisation){
                for (double a : allowance){
                    SmallStatistics[] _result = new SmallStatistics[6];
                    for (int i = 0; i < _result.length; i++) {
                        _result[i] = new SmallStatistics();
                    }
                    for (int ds = 0; ds < 50; ds++) {
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
                    } else if ("duniform".equals(dist)) {
                        distribution = "duniform";
                        param = 1;
                    }
                    meanTime = mean;
                    DynamicJSPFramework.revisit = true;
                    DynamicJSPFramework jspDynamic = new DynamicJSPFramework(SimSeed[ds],m,2,14,u,u,meanTime,distribution,param,500,2000);
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
                                newjob.getOperations()[i].setWait(correctValue(input.tempVal,input.stat.OT));
                            }
                            newjob.assignDuedate(a*newjob.getTotalProcessingTime());
                            newjob.setFinishTime(newjob.getReleaseTime()+input.partialEstimatedFlowtime);
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
                    result[0].add(jspDynamic.getMeanFlowtime());
                    result[1].add(jspDynamic.getCmax());
                    result[2].add(jspDynamic.getPercentTardiness());
                    result[3].add(jspDynamic.getMeantardiness());
                    result[4].add(jspDynamic.getMaxTardiness());
                    result[5].add(jspDynamic.getMAEE());
                    _result[0].add(jspDynamic.getMeanFlowtime());
                    _result[1].add(jspDynamic.getCmax());
                    _result[2].add(jspDynamic.getPercentTardiness());
                    _result[3].add(jspDynamic.getMeantardiness());
                    _result[4].add(jspDynamic.getMaxTardiness());
                    _result[5].add(jspDynamic.getMAEE());

                    detail += jspDynamic.getMeanFlowtime() + " " + jspDynamic.getCmax() + " " + jspDynamic.getPercentTardiness() + " " + jspDynamic.getMeantardiness() + " " + jspDynamic.getMaxTardiness()+ " " + correctValue(jspDynamic.getMAEE(),999999) + " ";
                    }
                    detail +="# \n +++" + _result[0].getAverage() + " " + _result[1].getAverage()  + " " + _result[2].getAverage() + " " + _result[3].getAverage() + " " + _result[4].getAverage() + " " + correctValue(_result[5].getAverage(),999999) + "\n";
                    }
                }}}}}
            return detail + "\n ***** " + result[0].getAverage() + " " + result[1].getAverage()  + " " + result[2].getAverage() + " " + result[3].getAverage() + " " + result[4].getAverage() + " " + correctValue(result[5].getAverage(),999999) + "\n";
            //return result[0].getAverage() + " " + result[1].getAverage() + " " + resultDD.getAverage();
        }

        private double correctValue(double val, double default_val){
            if (val < 0.0 || val > 99999 || Double.isNaN(val)){
                return default_val;
            }
            return val;
        }

}

/*
            double[] utilisation = {0.6,0.7,0.8,0.9,0.95};
            int[] numbeOfMachines = {4,5,6,10,20};
            String[] lowers = {"miss","full"};
            String[] dists = {"expo","erlang2","uniform"};
 */