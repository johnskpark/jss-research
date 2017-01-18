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
import ec.gp.koza.*;
import ec.simple.*;
import jsp.DynamicJSPFramework;
import jsp.Job;
import jsp.Machine;

public class GPjspDDOExtend extends GPProblem implements SimpleProblemForm {
    public static int[] SimSeed = {2734,72734,	72605,12628,20029,1991,
                            55013,84005,54972,80531,45414,25675,
                            79032,14882,17423,2798,77874,3805,
                            21671,51204,85187,76476,12363,92832,
                            36503,25237,26178,13614,50288,26279
                            };
    public static String fitness = "";
    public static String objective = "";

    public jspData input;

    //setting ************************************************
    public double meanTime = 1;
    Machine.priorityType PT = Machine.priorityType.cr_spt;
    public boolean LS = false;
    public int LS_iteration = 2500;
    //******* ************************************************

    public Object clone(){
        GPjspDDOExtend newobj = (GPjspDDOExtend) (super.clone());
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
            SmallStatistics result = new SmallStatistics();
            double[] utilisation = {0.7,0.8,0.9};
            int[] numbeOfMachines = {4,6};
            String[] lowers = {"miss"};
            String[] dists = {"expo"};
            //*
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
                    input.abjsp = jspDynamic;
                    //SampleArray sa = new SampleArray(20);
                    //set dispatching rule
                    jspDynamic.setPriorityType(PT);
                    jspDynamic.setScheduleStrategy(Machine.scheduleStrategy.NONDELAY);
                    jspDynamic.setNonDelayFactor(0.0);
                    //////////////////////////////////////////////
                    jspDynamic.setNextArrivalTime();
                    while (!jspDynamic.isStop()) {
                        if (jspDynamic.isNextArrivalEvent()) {
                            Job newjob = jspDynamic.generateRandomJob(jspDynamic.getNextArrivalTime());
                            input.stat.tempDuate = newjob.getTotalProcessingTime() + jspDynamic.getTotalNumberOfJobInQueue(newjob.getReleaseTime())*newjob.getNumberOperations()/(jspDynamic.getArrivalRate()*jspDynamic.getAverageNumberOfOperations());
                            input.partialEstimatedFlowtime = 0;
                            input.J = newjob;
                            for (int i = 0; i < newjob.getNumberOperations(); i++) {
                                input.stat.gatherStatFromJSPModel(jspDynamic, m , newjob,i , input.partialEstimatedFlowtime);
                                //calculcate parital flowtime
                                input.tempVal = 0;
                                input.k = i;
                                ((GPIndividual)ind).trees[0].child.eval(
                                    state,threadnum,input,stack,((GPIndividual)ind),this);
                                input.partialEstimatedFlowtime += input.tempVal;
                                //input.partialEstimatedFlowtime += input.stat.OT + input.stat.PRR*input.stat.QWL + input.stat.LOT ;
                            }
                            if (input.partialEstimatedFlowtime < 0.0 || input.partialEstimatedFlowtime
                                    > Double.POSITIVE_INFINITY || Double.isNaN(input.partialEstimatedFlowtime)){
                                result = new SmallStatistics();
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
                                jspDynamic.calculatePriority(M);
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
                    result.add(mpea);
                }

            }}}}
            //*/
            // the fitness better be KozaFitness!
            double MPEA = result.getAverage();
            //System.out.print(MPEA + "|");

            KozaFitness f = ((KozaFitness)ind.fitness);
            f.setStandardizedFitness(state,(float)MPEA);

            f.min = result.getMin();
            f.average = result.getAverage();
            f.max = result.getMax();

            ind.evaluated = true;
            System.out.print("|");
            }
     }
        public void finishEvaluating(final EvolutionState state, final int threadnum)
        {
            System.out.print("*");
            if (state.generation == state.numGenerations-1) {
                Individual best_i;
                SimpleShortStatistics stats = (SimpleShortStatistics) state.statistics;
                best_i = stats.getBestSoFar()[0];
                for(int y=1;y<state.population.subpops[0].individuals.length;y++)
                if (state.population.subpops[0].individuals[y].fitness.betterThan(best_i.fitness))
                    best_i = state.population.subpops[0].individuals[y];
                KozaFitness f = (KozaFitness)(best_i.fitness);
                f.results+= "\n" + getTestPerformance(state, threadnum, best_i);   //la
            } else if (state.generation%5==0){
                int best = 0;
                for(int y=0;y<state.population.subpops[0].individuals.length;y++){
                    if (state.population.subpops[0].individuals[y].fitness.betterThan(state.population.subpops[0].individuals[best].fitness))
                        best = y;
                }
                if (LS){
                //for (best = 0; best < state.population.subpops[0].individuals.length; best++) {
                    //state.population.subpops[0].individuals[best] = localsearch(state.population.subpops[0].individuals[best], state, threadnum);
                    //System.out.print("x");
                //}
                }
            }
            System.out.println("*");
        }
        public Individual localsearch(Individual ind,final EvolutionState state, final int threadnum){
            Individual[] inds = new Individual[1];
            BreedingSource[] mutate = {state.population.subpops[0].species.pipe_prototype.sources[3],
                state.population.subpops[0].species.pipe_prototype.sources[1]};
            int countTrap = 0;
            int maxTrap = 20;
            int kMutate = 0;
            for (int i = 0; i < LS_iteration; i++) {
                inds[0] = ind;
                mutate[kMutate].produce(1, 1, 0, 0, inds, state, threadnum);
                inds[0] = inds[0];
                inds[0].evaluated = false;
                evaluate(state, inds[0], threadnum, threadnum);
                if (inds[0].fitness.fitness()<ind.fitness.fitness()){
                    ind = inds[0];
                    countTrap = 0;
                } else {
                    countTrap++;
                    if (countTrap==maxTrap) {
                        kMutate = nextMutate(kMutate, mutate.length);
                        countTrap = 0;
                    }
                }
            }
            ind.evaluated = true;
            return ind;
        }
        public int nextMutate(int a, int max){
            if (a<max-1) return a+1;
            else return 0;
        }
        private String getTestPerformance(final EvolutionState state, final int threadnum, Individual best_i){
            SmallStatistics result = new SmallStatistics();
            String detailedReport = "\n dist s m u MPEA MPE ML STDL MT MAL MF PercentT \n";
            double[] utilisation = {0.6,0.7,0.8,0.9,0.95};
            int[] numbeOfMachines = {4,5,6,10,20};
            String[] lowers = {"miss","full"};
            String[] dists = {"expo","erlang2","uniform"};
            //*
            outerLoop:
            for (String dist : dists){for (String s : lowers){ for (int m : numbeOfMachines){ for (double u : utilisation){
                SmallStatistics MPEA = new SmallStatistics();
                SmallStatistics MPE = new SmallStatistics();
                SmallStatistics ML = new SmallStatistics();
                SmallStatistics STDL = new SmallStatistics();
                SmallStatistics MT = new SmallStatistics();
                SmallStatistics MAL = new SmallStatistics();
                SmallStatistics MF = new SmallStatistics();
                SmallStatistics PercentT = new SmallStatistics();
                String detailsStat = "";
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
                    input.abjsp = jspDynamic;
                    //SampleArray sa = new SampleArray(20);
                    //set dispatching rule
                    jspDynamic.setPriorityType(PT);
                    jspDynamic.setScheduleStrategy(Machine.scheduleStrategy.NONDELAY);
                    jspDynamic.setNonDelayFactor(0.0);
                    //////////////////////////////////////////////
                    jspDynamic.setNextArrivalTime();
                    while (!jspDynamic.isStop()) {
                        if (jspDynamic.isNextArrivalEvent()) {
                            Job newjob = jspDynamic.generateRandomJob(jspDynamic.getNextArrivalTime());
                            input.partialEstimatedFlowtime = 0;
                            input.J = newjob;
                            for (int i = 0; i < newjob.getNumberOperations(); i++) {
                                input.stat.gatherStatFromJSPModel(jspDynamic, m , newjob,i , input.partialEstimatedFlowtime);
                                //calculcate parital flowtime
                                input.tempVal = 0;
                                input.k = i;
                                ((GPIndividual)best_i).trees[0].child.eval(
                                    state,threadnum,input,stack,((GPIndividual)best_i),this);
                                input.partialEstimatedFlowtime += input.tempVal;
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
                                jspDynamic.calculatePriority(M);
                                if (M.getPlannedStartTimeNextOperation()<=jspDynamic.getNextArrivalTime()){
                                    Job J = M.completeJob();
                                    if (!J.isCompleted()) jspDynamic.machines[J.getCurrentMachine()].joinQueue(J);
                                    else jspDynamic.removeJobFromSystem(J);
                                } else
                                    M.plan();
                            } while(true);
                        }
                    }
                    double mape = jspDynamic.getMAPE();
                    detailsStat+=" "+mape;
                    if (mape < 0.0 || mape == Double.POSITIVE_INFINITY || Double.isNaN(mape))
                        break outerLoop;
                    result.add(mape);
                    MPEA.add(mape);
                    MPE.add(jspDynamic.getMPE());
                    ML.add(jspDynamic.getMeanLateness());
                    STDL.add(jspDynamic.getStdLateness());
                    MT.add(jspDynamic.getMeantardiness());
                    MAL.add(jspDynamic.getMAE());
                    MF.add(jspDynamic.getMeanFlowtime());
                    PercentT.add(jspDynamic.getPercentTardiness());
                }
                String setting = dist + " " + s + " " + m + " " + u + " ";
                detailedReport+=setting + MPEA.getAverage() + " " + MPE.getAverage() + " " +
                        ML.getAverage() + " " + STDL.getAverage() + " " + MT.getAverage() + " " +
                        MAL.getAverage() + " " + MF.getAverage() + " " + PercentT.getAverage() + detailsStat+ "\n";

            }}}}
                return "Performance on test set: [Average DEV = " + result.getAverage() +
                        ", Min DEV = " + result.getMin() + ", Max DEV = " +
                        result.getMax() + detailedReport;
        }
}

